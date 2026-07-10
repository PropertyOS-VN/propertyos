# PropertyOS

Hệ thống quản lý bất động sản cho thuê (nhà, văn phòng, chung cư, chung cư mini, phòng trọ). Tổ chức theo **multi-repo**: đây là repo gốc, 2 service nằm ở repo riêng và được gắn vào qua **git submodule** — tách để nhiều người làm việc song song không đụng code nhau, dù vẫn có 1 chỗ chứa docs/kiến trúc dùng chung.

Kiến trúc: **Next.js full-stack** (`apps/admin-app`) xử lý auth + CRUD tòa nhà/phòng/hợp đồng qua Supabase, **Spring Boot** (`apps/billing-service`) xử lý sinh hóa đơn định kỳ + tính toán tài chính — tối ưu cho free tier, và giữ trọn Java để thực hành.

## Tài liệu

- [Ý tưởng & phạm vi](docs/IDEA.md)
- [Kiến trúc hệ thống](docs/ARCHITECTURE.md)
- [Thiết kế database](docs/DATABASE.md)
- [Tech stack](docs/TECH_STACK.md)
- [Roadmap triển khai](docs/ROADMAP.md)

## Các repo trong tổ chức `PropertyOS-VN`

| Repo | Vai trò |
|---|---|
| [`propertyos`](https://github.com/PropertyOS-VN/propertyos) (repo này) | Docs, kiến trúc chung, AGENTS.md/CLAUDE.md, `.cursor/rules`, `.claude/skills`, docker-compose dev local |
| [`propertyos-admin-app`](https://github.com/PropertyOS-VN/propertyos-admin-app) | Next.js — app quản lý (chủ nhà/quản lý): auth, building, room, contract qua Supabase |
| [`propertyos-billing-service`](https://github.com/PropertyOS-VN/propertyos-billing-service) | Spring Boot — sinh hoá đơn, tính toán tài chính, deploy Cloud Run |

Dự kiến sau này thêm `propertyos-tenant-app` (hoặc tên tương tự) — app cho người thuê xem thông tin nhà/phòng để thuê, cũng sẽ là 1 repo + submodule riêng. Chưa tạo, sẽ bổ sung khi cần.

## Clone repo (lần đầu)

Vì 2 app nằm ở submodule, phải clone kèm `--recurse-submodules`, không thì `apps/admin-app` và `apps/billing-service` sẽ rỗng:

```bash
git clone --recurse-submodules https://github.com/PropertyOS-VN/propertyos.git
```

Nếu đã clone thường (quên `--recurse-submodules`), chạy bù:

```bash
git submodule update --init --recursive
```

## Làm việc với submodule

Mỗi submodule (`apps/admin-app`, `apps/billing-service`) là **1 git repo độc lập** — commit/push code bên trong nó như bình thường (`cd apps/admin-app && git add/commit/push`). Repo gốc (`propertyos`) chỉ lưu **con trỏ tới 1 commit cụ thể** của mỗi submodule; sau khi push submodule, quay lại repo gốc, `git add apps/admin-app` (hoặc `billing-service`) rồi commit + push để cập nhật con trỏ đó — nếu bỏ qua bước này, người khác clone repo gốc sẽ vẫn thấy submodule ở commit cũ.

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
