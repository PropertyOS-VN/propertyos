# billing-service

Service tính toán tài chính của PropertyOS — sinh hóa đơn định kỳ, tính tiền điện/nước/dịch vụ, báo cáo doanh thu. Đây là phần Java/Spring Boot duy nhất trong dự án (phần để bạn thực hành Java).

- **Stack**: Java 21, Spring Boot 3 (Web, Data JPA, Data MongoDB, Security + OAuth2 Resource Server, Validation)
- **DB**: Supabase (Postgres) cho `invoices`/`invoice_items`/`payments`/`contracts`; MongoDB Atlas cho `meter_readings`/`notification_logs`
- **Auth**: verify JWT do Supabase Auth phát hành (không tự phát hành token) — `web-app` gọi sang kèm `Authorization: Bearer <access_token>`
- **Hosting**: Google Cloud Run (free tier)

## Biến môi trường (đặt ở Cloud Run > Edit & deploy new revision > Variables)

```
SUPABASE_DB_URL=jdbc:postgresql://<host>:5432/postgres
SUPABASE_DB_USER=postgres
SUPABASE_DB_PASSWORD=<mật khẩu DB Supabase>
SUPABASE_JWKS_URL=https://<project-ref>.supabase.co/auth/v1/.well-known/jwks.json
MONGODB_ATLAS_URI=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/billing_service
RESEND_API_KEY=<api key Resend>
```

Lấy `SUPABASE_DB_URL`/user/password trong Supabase Dashboard > Project Settings > Database > Connection string (chọn "URI", đổi `postgresql://` thành `jdbc:postgresql://` cho Spring). `SUPABASE_JWKS_URL` lấy project-ref từ URL project Supabase.

## Chạy dev local

```bash
mvn spring-boot:run
```

Mặc định trỏ vào Postgres/Mongo local (`localhost`) nếu không set biến môi trường — dùng cùng `docker-compose.yml` ở root repo.

## Build & test Docker image trước khi deploy Cloud Run

```bash
docker build -t billing-service .
docker run -p 8082:8080 -e PORT=8080 billing-service
curl http://localhost:8082/health
```

## Deploy lên Cloud Run

```bash
gcloud run deploy billing-service \
  --source . \
  --region asia-southeast1 \
  --allow-unauthenticated \
  --set-env-vars SUPABASE_DB_URL=...,SUPABASE_DB_USER=...,SUPABASE_DB_PASSWORD=...,SUPABASE_JWKS_URL=...,MONGODB_ATLAS_URI=...
```

## Cấu trúc

```
src/main/java/com/propertyos/billing/
├── BillingServiceApplication.java   # @EnableScheduling cho job định kỳ
├── HealthController.java             # /health — public, cho Cloud Run healthcheck
├── config/SecurityConfig.java        # verify JWT Supabase qua JWKS
└── invoice/
    ├── InvoiceController.java         # POST /api/invoices/generate — web-app gọi thủ công
    └── InvoiceService.java            # logic sinh hoá đơn + job @Scheduled hàng tháng (TODO, xem ROADMAP.md Giai đoạn 4)
```

Xem roadmap chi tiết ở `../../docs/ROADMAP.md` (Giai đoạn 4).
