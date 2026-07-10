# Roadmap triển khai (v2)

## Giai đoạn 0 — Chuẩn bị (vài ngày)
- Tạo project Supabase (lấy connection string Postgres + API key + JWT secret).
- Tạo cluster MongoDB Atlas M0 (free).
- Tạo repo GitHub (`propertyos`, monorepo).
- Setup docker-compose local (Postgres + MongoDB) để dev offline.

## Giai đoạn 1 — Auth & schema nền tảng (1 tuần)
- Bật Supabase Auth (email/password trước, OAuth sau nếu cần).
- Tạo bảng `profiles`, RLS policy cơ bản theo role.
- Next.js: trang đăng nhập/đăng ký, middleware bảo vệ route theo session Supabase.
- **Milestone**: đăng nhập được, phân quyền theo role hoạt động.

## Giai đoạn 2 — Building/Room management (Next.js) (1-2 tuần)
- Tạo bảng `buildings`, `rooms` + RLS.
- Next.js Route Handlers/Server Actions: CRUD building/room.
- UI dashboard bằng HeroUI Pro block có sẵn.
- **Milestone**: quản lý được danh sách tòa nhà/phòng, đổi trạng thái phòng.

## Giai đoạn 3 — Contract management (Next.js) (1-2 tuần)
- Tạo bảng `tenants`, `contracts` + RLS.
- CRUD hợp đồng, validate phòng đang trống.
- **Milestone**: tạo/kết thúc/gia hạn hợp đồng qua UI.

## Giai đoạn 4 — Billing service (Java/Spring Boot) (3-4 tuần, phần học Java trọng tâm)
- Khởi tạo Spring Boot project, kết nối Supabase Postgres (JDBC) + MongoDB Atlas.
- Entity/repository cho `invoices`, `invoice_items`, `payments` (Postgres) và `meter_readings` (Mongo).
- API ghi chỉ số điện nước (`POST /meter-readings`).
- Job định kỳ (`@Scheduled`) sinh hóa đơn đầu kỳ cho các hợp đồng active, tính tiền thuê + điện + nước + dịch vụ.
- API ghi nhận thanh toán, cập nhật trạng thái invoice.
- Gửi email qua Resend sau khi sinh hóa đơn.
- Verify JWT Supabase cho request gọi từ `admin-app` (Spring Security).
- Deploy thử lên Google Cloud Run.
- **Milestone**: tạo hợp đồng ở Next.js → job Java tự sinh hóa đơn đúng kỳ → ghi nhận thanh toán → nhận được email.

## Giai đoạn 5 — Tích hợp & UI hóa đơn (Next.js) (1 tuần)
- Next.js đọc bảng `invoices`/`payments` (do billing-service ghi) để hiển thị danh sách hóa đơn, lịch sử thanh toán.
- Nút "Tạo hóa đơn thủ công" gọi API `billing-service`.

## Giai đoạn 6 — Deploy production (vài ngày)
- Deploy `apps/admin-app` lên Vercel (free), set biến môi trường Supabase.
- Deploy `apps/billing-service` lên Google Cloud Run (free tier), set biến môi trường Postgres/Mongo connection.
- Set Cloud Scheduler (nếu muốn kiểm soát lịch sinh hóa đơn từ ngoài thay vì `@Scheduled` cố định trong code).

## Giai đoạn 7 — Mở rộng (sau MVP)
- Portal khách thuê, thanh toán online (VNPay/Momo).
- Báo cáo doanh thu/tỷ lệ lấp đầy (mở rộng `billing-service`).
- Tách `billing-service` thành nhiều service nhỏ hơn nếu cần, hoặc thêm message queue thật khi traffic tăng.

## Tổng thời gian ước tính MVP
Khoảng **9-12 tuần**, làm một mình theo kiểu học dần Java song song với code Next.js.
