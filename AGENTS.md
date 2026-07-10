# PropertyOS — hướng dẫn chung cho AI coding agent (Claude Code, Cursor, ...)

Đây là monorepo gồm 2 phần độc lập, mỗi phần có bộ rule/skill riêng — **luôn đọc file trong thư mục con trước khi sửa code ở đó**:

- `apps/admin-app/AGENTS.md` — Next.js (FE + BE nhẹ), dùng Vercel Plugin (skill/rule chính thức)
- `apps/billing-service/AGENTS.md` — Spring Boot/Java (tính toán tài chính), rule tự viết theo `.claude/skills/spring-boot-conventions/`

## Quy tắc chung cho toàn repo

1. Đọc `docs/ARCHITECTURE.md`, `docs/DATABASE.md`, `docs/TECH_STACK.md` trước khi thêm tính năng mới liên quan tới kiến trúc/DB — đừng tự suy đoán schema.
2. Không tạo lại NestJS/MariaDB/RabbitMQ hay bất kỳ service nào ngoài `apps/admin-app` và `apps/billing-service` — các thư mục `apps/auth-service`, `apps/building-service`, `apps/gateway-service` đã DEPRECATED, chỉ giữ để tham khảo, không sửa/không build.
3. `apps/admin-app` và `apps/billing-service` chia sẻ chung Supabase (Postgres) + MongoDB Atlas — không tạo thêm database riêng cho mỗi service.
4. Không commit secret (Supabase key, Mongo URI, JWT secret...) — luôn dùng biến môi trường + file `.env.example` làm mẫu.
5. Trước khi sửa dependency version (Next.js, React, Spring Boot...), kiểm tra bản mới nhất thực tế thay vì dùng version cũ trong training data.
6. `apps/admin-app` dùng **pnpm** làm package manager (xem `packageManager` trong `package.json`) — luôn dùng `pnpm install`/`pnpm add`/`pnpm dlx`, không dùng `npm`/`yarn`/`npx` và không commit `package-lock.json`/`yarn.lock`.

## Cấu trúc AGENTS.md/CLAUDE.md trong repo này

Mỗi thư mục (`root`, `apps/admin-app`, `apps/billing-service`) có 1 cặp file:
- `AGENTS.md` — nguồn nội dung thật (đọc bởi Cursor, Codex, và các agent hỗ trợ chuẩn AGENTS.md)
- `CLAUDE.md` — chỉ chứa `@AGENTS.md` để Claude Code import cùng nội dung, tránh trùng lặp

Nếu sửa rule, luôn sửa ở `AGENTS.md`, không sửa `CLAUDE.md`.
