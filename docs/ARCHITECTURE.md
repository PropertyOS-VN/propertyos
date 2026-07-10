# Kiến trúc hệ thống (v2 — Next.js full-stack + Java hybrid, tối ưu free tier)

## 1. Thành phần

| Thư mục | Vai trò | Ngôn ngữ/Framework | Hosting |
|---|---|---|---|
| `apps/admin-app` | FE + BE nhẹ: đăng nhập, quản lý tòa nhà/phòng, hợp đồng, xem hóa đơn | Next.js (App Router) + React + Tailwind + Framer Motion + HeroUI v2 + HeroUI Pro | Vercel (free) |
| `apps/billing-service` | Sinh hóa đơn định kỳ, tính tiền điện/nước/dịch vụ, báo cáo tài chính | Java 25 + Spring Boot 4 | Google Cloud Run (free tier) |

Không còn NestJS, không còn tách 4 service riêng — gộp về 2 thành phần để vừa dễ deploy free, vừa giữ trọn Java cho phần cần học/thực hành.

## 2. Vì sao chia như vậy

Next.js Route Handlers/Server Actions trên Vercel free tier bị giới hạn ~10s/request và không phù hợp cho job chạy nền dài hay tính toán nặng lặp lại theo lịch (VD: sinh hóa đơn cho toàn bộ hợp đồng vào đầu tháng). Những phần đó được tách sang `billing-service` (Java, Spring Boot) chạy trên Cloud Run — không giới hạn 10s, có thể dùng Spring Scheduler/Cloud Scheduler để chạy job định kỳ, và tận dụng Spring Data JPA cho các phép tính tài chính cần transaction chặt chẽ.

Các thao tác CRUD đơn giản, phản hồi nhanh (tòa nhà, phòng, hợp đồng, đăng nhập) thì Next.js xử lý trực tiếp qua Supabase client — không cần thêm 1 tầng service riêng, giảm độ trễ và độ phức tạp.

## 3. Sơ đồ luồng

```
┌─────────────────────────────┐
│   apps/admin-app (Next.js)     │   Vercel (free)
│   - UI (HeroUI Pro)          │
│   - Auth (Supabase Auth)     │
│   - CRUD: building/room/     │
│     contract (Supabase JS)   │
└──────────┬───────────────────┘
           │ REST call (khi cần tính toán/job)
           ▼
┌─────────────────────────────┐
│  apps/billing-service        │   Google Cloud Run (free tier)
│  (Spring Boot)                │
│  - Sinh hóa đơn định kỳ       │
│  - Tính tiền điện/nước        │
│  - Ghi nhận thanh toán        │
│  - Báo cáo doanh thu          │
└───────┬──────────────┬────────┘
        │              │
        ▼              ▼
┌───────────────┐  ┌──────────────────┐
│ Supabase        │  │ MongoDB Atlas      │
│ (Postgres)      │  │ (M0 free cluster)  │
│ - profiles      │  │ - meter_readings    │
│ - buildings     │  │ - room_attributes   │
│ - rooms         │  │ - notification_logs │
│ - tenants       │  └──────────────────┘
│ - contracts     │
│ - invoices      │
│ - invoice_items │
│ - payments      │
└───────────────┘
        ▲
        │ Supabase JS client (đọc/ghi trực tiếp)
        │
   apps/admin-app cũng đọc/ghi thẳng vào Postgres
   cho các thao tác CRUD đơn giản (không qua billing-service)
```

## 4. Xác thực & phân quyền

Dùng thẳng **Supabase Auth** (JWT built-in, hỗ trợ email/password + OAuth) — không tự viết auth-service nữa. Vai trò (`OWNER`, `MANAGER`, `ACCOUNTANT`) lưu ở bảng `profiles` (Postgres), liên kết 1-1 với `auth.users` của Supabase. `billing-service` verify JWT của Supabase (JWKS endpoint) để xác thực request gọi từ `admin-app`.

## 5. Thông báo (email hóa đơn mới, nhắc thanh toán)

Không dùng RabbitMQ. Sau khi `billing-service` sinh hóa đơn xong, gọi thẳng 1 email API (VD: Resend, có free tier) để gửi email — đồng bộ, đơn giản, đủ dùng ở quy mô cá nhân. Log lại vào `notification_logs` (MongoDB).

## 6. Hạ tầng dev local

`docker-compose.yml` ở root chỉ chạy Postgres + MongoDB cho môi trường dev (mô phỏng Supabase/Atlas cục bộ, không cần internet khi code). `billing-service` có Dockerfile riêng để test build trước khi deploy Cloud Run.

## 7. Roadmap mở rộng sau này

Nếu traffic thật tăng và cần tách `billing-service` thành nhiều service nhỏ hơn (VD: tách riêng phần báo cáo), hoặc cần message queue thật (Kafka/RabbitMQ) khi khối lượng event lớn, kiến trúc hiện tại vẫn dễ mở rộng vì đã tách rõ theo domain (`admin-app` vs `billing-service`) và dùng Postgres/Mongo chuẩn (không khoá vào Supabase/Atlas cụ thể, có thể tự host lại khi cần).
