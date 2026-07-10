# PropertyOS

Monorepo hệ thống quản lý bất động sản cho thuê (nhà, văn phòng, chung cư, chung cư mini, phòng trọ).

Kiến trúc: **Next.js full-stack** (`apps/web-app`) xử lý auth + CRUD tòa nhà/phòng/hợp đồng qua Supabase, **Spring Boot** (`apps/billing-service`) xử lý sinh hóa đơn định kỳ + tính toán tài chính — tối ưu cho free tier, và giữ trọn Java để thực hành.

## Tài liệu

- [Ý tưởng & phạm vi](docs/IDEA.md)
- [Kiến trúc hệ thống](docs/ARCHITECTURE.md)
- [Thiết kế database](docs/DATABASE.md)
- [Tech stack](docs/TECH_STACK.md)
- [Roadmap triển khai](docs/ROADMAP.md)

## Cấu trúc repo (monorepo)

```
property-mgmt-root/
├── docs/                     # toàn bộ tài liệu thiết kế
├── docker-compose.yml        # Postgres + MongoDB cho dev local (mô phỏng Supabase/Atlas)
└── apps/
    ├── web-app/               # Next.js — FE + BE nhẹ (auth, building, room, contract qua Supabase)
    ├── billing-service/       # Spring Boot — sinh hoá đơn, tính toán tài chính, deploy Cloud Run
    ├── auth-service/          # DEPRECATED — đã gộp vào web-app (Supabase Auth)
    ├── building-service/      # DEPRECATED — đã gộp vào web-app
    └── gateway-service/       # DEPRECATED — không cần gateway riêng nữa
```

Các thư mục đánh dấu DEPRECATED chỉ giữ lại để tham khảo thiết kế cũ (NestJS/MariaDB/RabbitMQ), không build/deploy — chi tiết lý do đổi hướng xem `docs/ARCHITECTURE.md`.

## Sau khi tạo repo trên GitHub

1. Tạo 1 repo trên GitHub: `propertyos` (private).
2. Push toàn bộ monorepo:
   ```bash
   cd property-mgmt-root && git remote add origin git@github.com:<your-username>/propertyos.git && git push -u origin main
   ```

## Chạy dev local (Postgres + MongoDB + billing-service)

```bash
docker compose up -d --build
curl http://localhost:8082/health   # billing-service
```

`apps/web-app` chạy riêng bằng `npm run dev` (xem `apps/web-app/README.md`), không nằm trong docker-compose vì Next.js dev server chạy trực tiếp là đủ nhanh, không cần container hoá lúc dev.

## Deploy production

| Thành phần | Nền tảng | Ghi chú |
|---|---|---|
| `apps/web-app` | [Vercel](https://vercel.com) (free) | Root Directory = `apps/web-app` |
| `apps/billing-service` | [Google Cloud Run](https://cloud.google.com/run) (free tier) | `gcloud run deploy` — xem `apps/billing-service/README.md` |
| Postgres + Auth | [Supabase](https://supabase.com) (free) | |
| MongoDB | [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) M0 (free vĩnh viễn) | |

Dừng dev local: `docker compose down` (thêm `-v` nếu muốn xoá luôn volume dữ liệu).
