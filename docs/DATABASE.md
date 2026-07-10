# Thiết kế Database (v2 — Supabase Postgres + MongoDB Atlas)

Nguyên tắc: **Supabase (Postgres)** cho dữ liệu quan hệ, cần transaction chặt (user/role, building, room, contract, invoice, payment). **MongoDB Atlas** cho dữ liệu bán cấu trúc, thay đổi schema thường xuyên hoặc log (chỉ số điện nước theo kỳ, thuộc tính phòng tùy loại hình, log thông báo).

Cả `apps/web-app` (Next.js, qua Supabase JS client) và `apps/billing-service` (Spring Boot, qua JDBC + Spring Data MongoDB) đều kết nối vào cùng 2 DB này — không còn database-per-service như bản thiết kế cũ, vì quy mô hiện tại (dự án cá nhân) không cần cách ly DB theo service.

## 1. Supabase (Postgres)

**profiles** (mở rộng từ `auth.users` của Supabase, liên kết 1-1 qua `id`)
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | uuid PK | = `auth.users.id` |
| full_name | text | |
| phone | varchar(20) | |
| role | text (check: OWNER/MANAGER/ACCOUNTANT) | |
| created_at | timestamptz | default now() |

**buildings**
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | bigint PK (identity) | |
| owner_id | uuid | FK → profiles.id |
| name | text | |
| address | text | |
| type | text (check: HOUSE/OFFICE/APARTMENT/MINI_APARTMENT/BOARDING_HOUSE) | |
| status | text (check: ACTIVE/INACTIVE) | default 'ACTIVE' |
| created_at | timestamptz | default now() |

**rooms**
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | bigint PK (identity) | |
| building_id | bigint | FK → buildings.id |
| code | text | VD "P101" |
| floor | int | |
| area_m2 | numeric(8,2) | |
| base_price | numeric(14,2) | |
| status | text (check: AVAILABLE/OCCUPIED/MAINTENANCE) | default 'AVAILABLE' |
| created_at | timestamptz | default now() |

**tenants** (hồ sơ khách thuê)
| Cột | Kiểu |
|---|---|
| id | bigint PK (identity) |
| full_name | text |
| id_card_number | text |
| phone | text |
| email | text |

**contracts**
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | bigint PK (identity) | |
| room_id | bigint | FK → rooms.id |
| tenant_id | bigint | FK → tenants.id |
| start_date | date | |
| end_date | date | |
| deposit_amount | numeric(14,2) | |
| rent_amount | numeric(14,2) | |
| billing_cycle | text (check: MONTHLY/QUARTERLY) | default 'MONTHLY' |
| status | text (check: DRAFT/ACTIVE/TERMINATED/EXPIRED) | default 'DRAFT' |

**invoices** (ghi bởi `billing-service`, đọc bởi `web-app`)
| Cột | Kiểu | Ghi chú |
|---|---|---|
| id | bigint PK (identity) | |
| contract_id | bigint | FK → contracts.id |
| period | text | VD "2026-07" |
| due_date | date | |
| total_amount | numeric(14,2) | |
| status | text (check: PENDING/PARTIALLY_PAID/PAID/OVERDUE) | default 'PENDING' |

**invoice_items** (id, invoice_id FK, type text check RENT/ELECTRICITY/WATER/SERVICE_FEE/OTHER, quantity, unit_price, amount)

**payments** (id, invoice_id FK, amount, method text check CASH/BANK_TRANSFER/MOMO/VNPAY, paid_at timestamptz)

Bật **Row Level Security (RLS)** trên toàn bộ bảng — vì `web-app` gọi thẳng Supabase từ client, RLS là lớp bảo vệ bắt buộc (VD: Manager chỉ thấy building mình quản lý). `billing-service` dùng service-role key (bypass RLS) vì chạy phía server tin cậy.

## 2. MongoDB Atlas (M0 free cluster)

**meter_readings** — ghi chỉ số điện nước theo kỳ, do `billing-service` ghi (và đọc lại khi tính hóa đơn):
```json
{
  "room_id": 101,
  "period": "2026-07",
  "electricity": { "previous": 120, "current": 145 },
  "water": { "previous": 30, "current": 34 },
  "recorded_by": "uuid-of-manager",
  "recorded_at": "2026-07-05T08:00:00Z",
  "photo_url": null
}
```

**room_attributes** — thuộc tính linh hoạt theo loại hình bất động sản, do `web-app` đọc/ghi trực tiếp:
```json
{
  "room_id": 101,
  "building_type": "BOARDING_HOUSE",
  "attributes": { "max_occupants": 2, "has_private_bathroom": true }
}
```

**notification_logs** — do `billing-service` ghi sau khi gửi email:
```json
{ "user_id": "uuid", "channel": "EMAIL", "template": "invoice_created", "status": "SENT", "sent_at": "2026-07-05T08:05:00Z" }
```

## 3. Sơ đồ quan hệ mức ý tưởng (ERD rút gọn)

```
profiles (1) ── (n) buildings (1) ── (n) rooms (1) ── (0..1 active) contracts
                                                              │
tenants (1) ── (n) contracts (1) ── (n) invoices (1) ── (n) invoice_items
                                              │
                                        (n) payments
```

## 4. Vì sao chọn Supabase + MongoDB Atlas thay vì MariaDB + MongoDB tự host

Supabase cho free tier có sẵn Postgres quản lý (không cần tự vận hành DB server), kèm Auth built-in nên bỏ được hẳn 1 service auth riêng. MongoDB Atlas M0 là free tier vĩnh viễn (512MB, không giới hạn thời gian) — đủ cho log/dữ liệu linh hoạt ở quy mô cá nhân. Cả hai đều dùng chuẩn Postgres/Mongo thông thường nên nếu sau này cần tự host lại (khi vượt free tier), việc migrate không bị khóa vào 1 nhà cung cấp cụ thể.
