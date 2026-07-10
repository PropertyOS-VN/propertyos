# Tech Stack (v2 — tối ưu free tier + học Java)

## Frontend + BE nhẹ (`apps/admin-app`)

| Thành phần | Lựa chọn | Ghi chú |
|---|---|---|
| Framework | Next.js (latest, App Router) | Route Handlers/Server Actions cho CRUD đơn giản |
| UI library | React (latest) | |
| Styling | Tailwind CSS (latest) | |
| Animation | Framer Motion | peer dependency bắt buộc của HeroUI v2, giữ nguyên tên gói `framer-motion` |
| Component kit | HeroUI (v2.heroui.com) + HeroUI Pro (đã có license) | |
| Data/Auth client | `@supabase/supabase-js` + `@supabase/ssr` | Auth (session cookie) + query Postgres trực tiếp |
| Form | React Hook Form + Zod | |

## Backend tính toán (`apps/billing-service`)

| Thành phần | Lựa chọn | Ghi chú |
|---|---|---|
| Ngôn ngữ | Java 25 (LTS) | |
| Framework | Spring Boot 4 | |
| Data (Postgres) | Spring Data JPA + driver `org.postgresql:postgresql` | Kết nối Supabase Postgres qua connection string chuẩn |
| Data (Mongo) | Spring Data MongoDB | Kết nối MongoDB Atlas qua connection string |
| Job định kỳ | Spring Scheduler (`@Scheduled`) | Sinh hóa đơn đầu kỳ, có thể nâng lên Cloud Scheduler gọi endpoint nếu muốn kiểm soát lịch từ ngoài |
| Xác thực request từ admin-app | Spring Security + JWT (verify JWKS của Supabase) | Không tự phát hành token, chỉ verify token Supabase |
| Email | Resend (hoặc SMTP) | Gửi email hóa đơn/nhắc thanh toán |

## Cơ sở dữ liệu & hạ tầng

| Thành phần | Lựa chọn | Free tier |
|---|---|---|
| Postgres + Auth | Supabase | 500MB DB, 50.000 MAU auth, 1GB storage |
| NoSQL | MongoDB Atlas (M0) | 512MB, vĩnh viễn miễn phí |
| FE hosting | Vercel (Hobby) | 100GB băng thông, 100k function invocations/tháng |
| BE hosting | Google Cloud Run | 2 triệu request/tháng, miễn phí vĩnh viễn (không như trial credit) |
| Local dev | Docker Compose (Postgres + MongoDB local) | Không tốn phí, mô phỏng Supabase/Atlas khi code offline |

## Vì sao chọn stack này

Next.js xử lý thẳng phần CRUD đơn giản qua Supabase — không cần thêm tầng NestJS/API riêng, giảm số thành phần phải deploy và maintain. Toàn bộ phần "khó" về nghiệp vụ (tính hóa đơn, job định kỳ, báo cáo tài chính) dồn về 1 service Java duy nhất — vừa dùng đúng thế mạnh của Spring Boot (transaction, batch, scheduler), vừa cho bạn thực hành Java trọn vẹn thay vì chia nhỏ giữa nhiều ngôn ngữ. Google Cloud Run được chọn thay Render vì free tier bền hơn (không giới hạn theo tháng kiểu trial, không cần lo hết credit).
