# PropertyOS

Monorepo hệ thống quản lý bất động sản cho thuê (nhà, văn phòng, chung cư, chung cư mini, phòng trọ).

Kiến trúc: **Next.js full-stack** (`apps/admin-app`) xử lý auth + CRUD tòa nhà/phòng/hợp đồng qua Supabase, **Spring Boot** (`apps/billing-service`) xử lý sinh hóa đơn định kỳ + tính toán tài chính — tối ưu cho free tier, và giữ trọn Java để thực hành.

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
    ├── admin-app/               # Next.js — app quản lý (chủ nhà/quản lý): auth, building, room, contract qua Supabase
    ├── billing-service/       # Spring Boot — sinh hoá đơn, tính toán tài chính, deploy Cloud Run
    ├── auth-service/          # DEPRECATED — đã gộp vào admin-app (Supabase Auth)
    ├── building-service/      # DEPRECATED — đã gộp vào admin-app
    └── gateway-service/       # DEPRECATED — không cần gateway riêng nữa
```

Dự kiến sau này thêm `apps/tenant-app` (hoặc tên tương tự) — app cho người thuê xem thông tin nhà/phòng để thuê, tách riêng khỏi `admin-app`. Chưa scaffold, sẽ bổ sung khi cần.

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

`apps/admin-app` chạy riêng bằng `pnpm dev` (xem `apps/admin-app/README.md`), không nằm trong docker-compose vì Next.js dev server chạy trực tiếp là đủ nhanh, không cần container hoá lúc dev.

## Deploy production

| Thành phần | Nền tảng | Ghi chú |
|---|---|---|
| `apps/admin-app` | [Vercel](https://vercel.com) (free) | Root Directory = `apps/admin-app` |
| `apps/billing-service` | [Google Cloud Run](https://cloud.google.com/run) (free tier) | `gcloud run deploy` — xem `apps/billing-service/README.md` |
| Postgres + Auth | [Supabase](https://supabase.com) (free) | |
| MongoDB | [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) M0 (free vĩnh viễn) | |

Dừng dev local: `docker compose down` (thêm `-v` nếu muốn xoá luôn volume dữ liệu).

## Skill & rule cho AI coding agent (Claude Code + Cursor)

Repo dùng chuẩn [AGENTS.md](https://agents.md) làm nguồn nội dung chung, mỗi `CLAUDE.md` chỉ chứa `@AGENTS.md` để import (không trùng lặp nội dung). Cursor đọc thêm `.cursor/rules/*.mdc`.

| File | Áp dụng cho | Nội dung |
|---|---|---|
| `AGENTS.md` (root) | Toàn repo | Quy tắc chung, cấu trúc monorepo |
| `apps/admin-app/AGENTS.md` | Next.js | Snippet chính thức của Next.js + hướng dẫn dùng Vercel Plugin |
| `apps/billing-service/AGENTS.md` | Spring Boot | Quy tắc Java/Spring Boot riêng cho project |
| `.claude/skills/spring-boot-conventions/SKILL.md` | Claude Code | Skill chi tiết cho `billing-service` (kiến trúc, transaction, test...) |
| `.cursor/rules/*.mdc` | Cursor | `general.mdc` (luôn áp dụng), `frontend-nextjs.mdc`/`backend-spring-boot.mdc` (tự động áp dụng theo glob đúng thư mục) |

**FE**: dùng [Vercel Plugin](https://vercel.com/docs/agent-resources/vercel-plugin) chính thức thay vì tự viết rule — cài 1 lần trong `apps/admin-app`:

```bash
cd apps/admin-app
pnpm dlx plugins add vercel/vercel-plugin
```

Hỗ trợ cả Claude Code, Cursor, Codex, Copilot. 25 skill có sẵn (Next.js, React best practices, Cache Components, Turbopack, deploy...), gọi bằng `/vercel-plugin:<skill>`.

**BE**: Spring Boot chưa có plugin chính thức tương đương, nên rule tự viết dựa theo cấu trúc thực tế của `billing-service` (xem file trong bảng trên). Khi sửa rule, sửa ở `AGENTS.md`/`.mdc`, không sửa `CLAUDE.md`.
