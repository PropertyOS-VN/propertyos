# Backlog — việc tiếp theo

Danh sách task để tạo issue trên GitHub. Mỗi mục `###` tương ứng 1 issue — copy tiêu đề + mô tả bên dưới vào issue là dùng được luôn. Label/milestone gợi ý ghi cuối mỗi mục.

## Nhóm 1 — Repo/org hygiene (root `propertyos`)

Làm trước vì rẻ, và mở khóa việc làm nhóm khi có thêm người join.

### Tạo GitHub Projects board

**Vì sao:** hiện chưa có nơi theo dõi tiến độ chung giữa 3 repo (`propertyos`, `propertyos-admin-app`, `propertyos-billing-service`) — issue tạo ra dễ bị lạc, không biết cái nào đang làm.

**Việc cần làm:**
- Tạo 1 Project (Board view) ở cấp Organization `PropertyOS-VN`, không tạo riêng theo từng repo.
- Cột: `Backlog` → `To do` → `In progress` → `In review` → `Done`.
- Link cả 3 repo vào Project để issue/PR ở repo nào cũng tự động hiện lên board.

**Tiêu chí hoàn thành:**
- Tạo 1 issue test ở mỗi repo, xác nhận cả 3 đều lên board.

Label: `chore` · Milestone: (không cần milestone, việc chạy 1 lần)

---

### Chuẩn hoá labels

**Vì sao:** chưa có quy ước label, issue tạo ra sẽ không phân loại được theo loại việc/khu vực/độ ưu tiên.

**Việc cần làm:**
- Tạo labels theo 3 nhóm, đồng bộ cả 3 repo:
  - Loại việc: `type:feature`, `type:bug`, `type:chore`, `type:docs`
  - Khu vực: `area:admin-app`, `area:billing-service`, `area:docs`
  - Độ ưu tiên: `priority:p0`, `priority:p1`, `priority:p2`
- Xoá bớt label mặc định của GitHub không dùng tới (`wontfix`, `question`... tuỳ nhu cầu).

**Tiêu chí hoàn thành:**
- 3 repo có cùng bộ label, tên/màu giống nhau.

Label: `chore` · Milestone: (không cần)

---

### Tạo milestones theo Giai đoạn trong ROADMAP.md

**Vì sao:** `docs/ROADMAP.md` đã chia 7 giai đoạn nhưng chưa map sang milestone trên GitHub — không đo được tiến độ từng giai đoạn.

**Việc cần làm:**
- Tạo milestone `Giai đoạn 1 — Auth & schema`, `Giai đoạn 2 — Building/Room`, `Giai đoạn 3 — Contract`, ... khớp với `docs/ROADMAP.md`.
- Set due date tham khảo theo ước tính thời gian ghi trong ROADMAP (không bắt buộc cứng).
- Gán milestone cho các issue ở Nhóm 3, 4 bên dưới.

**Tiêu chí hoàn thành:**
- Milestone tạo đủ ở repo `propertyos-admin-app` (chứa hầu hết việc Giai đoạn 1-3, 5) và `propertyos-billing-service` (Giai đoạn 4).

Label: `chore` · Milestone: (không cần)

---

### Thêm file CODEOWNERS

**Vì sao:** hiện tại chưa có review bắt buộc/reviewer mặc định — khi có thêm người join, PR có thể merge mà không ai review.

**Việc cần làm:**
- Thêm `.github/CODEOWNERS` ở mỗi repo (root + 2 submodule), gán bản thân làm owner mặc định (`* @<github-username>`).
- Khi có thêm người, phân theo path (VD: `apps/admin-app/src/lib/supabase/ @<username>`).
- Bật branch protection rule trên `main` yêu cầu review trước khi merge (Settings → Branches).

**Tiêu chí hoàn thành:**
- Tạo PR test, xác nhận GitHub tự request review đúng người theo CODEOWNERS.

Label: `chore` · Milestone: (không cần)

---

### Thêm `.env.example` cho `billing-service`

**Vì sao:** repo hiện chỉ có `.env.local` chứa giá trị thật (đã gitignore), nhưng chưa có file mẫu `.env.example` — người mới clone không biết cần khai báo biến gì, và không có gì nhắc nhở tránh commit nhầm `.env.local`.

**Việc cần làm:**
- Tạo `apps/billing-service/.env.example` liệt kê các biến đang dùng trong `application.yml`: `PORT`, `SUPABASE_DB_URL`, `SUPABASE_DB_USER`, `SUPABASE_DB_PASSWORD`, `MONGODB_ATLAS_URI`, `SUPABASE_JWKS_URL`, `RESEND_API_KEY` — để giá trị placeholder, không phải giá trị thật.
- Xác nhận `.env.local` đã có trong `.gitignore` của `billing-service` (kiểm tra lại, vì hiện `.env.local` đang tồn tại trên disk).

**Tiêu chí hoàn thành:**
- `git status` không thấy `.env.local` bị track.
- `.env.example` đủ biến để người mới copy sang `.env.local` và chạy được.

Label: `chore`, `priority:p1` (liên quan bảo mật secret) · Milestone: (không cần)

---

### Bật bắt buộc 2FA cho org `PropertyOS-VN`

**Vì sao:** org quản lý code cho dự án thật, nên bắt buộc 2FA để tránh mất quyền truy cập nếu tài khoản thành viên bị chiếm.

**Việc cần làm:**
- Vào Organization settings → Authentication security → bật "Require two-factor authentication".
- Nếu có thành viên chưa bật 2FA, họ sẽ bị remove khỏi org cho tới khi bật — cần thông báo trước.

**Tiêu chí hoàn thành:**
- Setting đã bật, xác nhận qua Organization settings.

Label: `chore`, `priority:p1` · Milestone: (không cần)

## Nhóm 2 — Hạ tầng còn thiếu (nốt Giai đoạn 0)

### Xác nhận/tạo Supabase project thật

**Vì sao:** `docs/ROADMAP.md` Giai đoạn 0 yêu cầu có Supabase project thật (Postgres + Auth + API key + JWT secret), cần xác nhận đã tạo hay còn đang dùng local docker-compose.

**Việc cần làm:**
- Tạo project trên supabase.com (free tier) nếu chưa có.
- Lấy: Project URL, `anon` key, `service_role` key, JWT secret, Postgres connection string.
- Điền vào `apps/admin-app/.env.local` (không commit) theo mẫu `.env.example` hiện có.
- Điền `SUPABASE_DB_*`, `SUPABASE_JWKS_URL` vào `apps/billing-service/.env.local`.

**Tiêu chí hoàn thành:**
- `admin-app` chạy `pnpm dev`, gọi Supabase thành công (thử 1 query bất kỳ).
- `billing-service` connect được Postgres qua Supabase (thay vì docker-compose local).

Label: `type:chore`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Xác nhận/tạo MongoDB Atlas M0 cluster

**Vì sao:** tương tự trên, cần cluster Mongo thật để `billing-service` lưu `meter_readings`.

**Việc cần làm:**
- Tạo cluster M0 (free vĩnh viễn) trên MongoDB Atlas nếu chưa có.
- Whitelist IP (hoặc cho phép `0.0.0.0/0` tạm thời lúc dev, siết lại khi deploy Cloud Run).
- Lấy connection string, điền `MONGODB_ATLAS_URI` vào `.env.local` của `billing-service`.

**Tiêu chí hoàn thành:**
- `billing-service` connect được Mongo Atlas, không lỗi timeout khi start app.

Label: `type:chore`, `area:billing-service` · Milestone: Giai đoạn 1

---

### Link Vercel project cho `admin-app`

**Vì sao:** deploy production cho `admin-app` cần link repo `propertyos-admin-app` (không phải repo gốc `propertyos`) vào Vercel.

**Việc cần làm:**
- Import repo `propertyos-admin-app` vào Vercel (không phải `propertyos`, vì đây là repo submodule độc lập).
- Set Root Directory (nếu Vercel yêu cầu — thường để trống vì repo submodule đã là root của chính nó).
- Set biến môi trường Supabase trên Vercel dashboard.

**Tiêu chí hoàn thành:**
- Deploy preview thành công, trang chủ load được, `/api/health` trả `{status:"ok"}`.

Label: `type:chore`, `area:admin-app` · Milestone: Giai đoạn 6 (deploy) — có thể làm sớm hơn để có preview URL

## Nhóm 3 — Giai đoạn 1: Auth & schema nền tảng (ưu tiên ngay tiếp theo)

### Migration bảng `profiles` + RLS policy theo role

**Vì sao:** chưa có bảng nào trong Postgres — đây là bảng nền tảng đầu tiên, lưu thông tin user (role: owner/manager) gắn với `auth.users` của Supabase.

**Việc cần làm:**
- Viết migration SQL tạo bảng `profiles` (`id` = `auth.users.id`, `full_name`, `role`, `created_at`).
- Bật RLS: user chỉ đọc/sửa được row của chính mình; role `owner` đọc được tất cả (dùng cho quản lý sau này).
- Trigger tự tạo `profiles` row khi có user mới đăng ký (`on auth.users insert`).
- Cập nhật `docs/DATABASE.md` nếu schema khác với bản đã viết.

**Tiêu chí hoàn thành:**
- Đăng ký user mới qua Supabase Auth → tự động có row `profiles` tương ứng.
- Query bằng 1 user thường không đọc được `profiles` của user khác (test RLS).

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Bật Supabase Auth (email/password)

**Vì sao:** cần bật provider trước khi code trang đăng nhập.

**Việc cần làm:**
- Vào Supabase dashboard → Authentication → bật Email provider, tắt các provider không dùng.
- Cấu hình email template (xác nhận đăng ký) — tối thiểu để tiếng Việt cho phần nội dung.
- Set redirect URL cho local (`http://localhost:3000`) và sau này thêm URL Vercel production.

**Tiêu chí hoàn thành:**
- Đăng ký thử 1 tài khoản qua Supabase dashboard, nhận được email xác nhận.

Label: `type:chore`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Trang đăng ký/đăng nhập

**Vì sao:** đây là màn hình đầu tiên user thấy — chưa có UI auth nào trong `admin-app` hiện tại.

**Việc cần làm:**
- Ưu tiên tìm HeroUI Pro auth block có sẵn (theo rule trong `AGENTS.md`) thay vì tự vẽ form từ đầu.
- Trang `/login`, `/register` — Client Component, gọi `src/lib/supabase/client.ts`.
- Validate form bằng `react-hook-form` + `zod` (đã có sẵn trong dependencies).
- Xử lý lỗi (sai mật khẩu, email đã tồn tại) hiển thị message tiếng Việt.

**Tiêu chí hoàn thành:**
- Đăng ký + đăng nhập được từ UI, redirect vào dashboard sau khi login thành công.

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Middleware bảo vệ route theo session Supabase

**Vì sao:** hiện chưa có middleware nào — mọi route đang public, cần chặn truy cập dashboard khi chưa đăng nhập.

**Việc cần làm:**
- Tạo `middleware.ts` ở `admin-app`, check session qua `@supabase/ssr`.
- Route `/login`, `/register`, `/api/health` là public; còn lại yêu cầu đã đăng nhập, chưa login thì redirect về `/login`.
- Đã login mà vào `/login` thì redirect thẳng vào dashboard (tránh vòng lặp UX khó chịu).

**Tiêu chí hoàn thành:**
- Test thủ công: xoá cookie session, truy cập route bảo vệ → bị đá về `/login`.

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Xử lý callback sau login/logout

**Vì sao:** Supabase Auth cần route callback để đổi code lấy session (đặc biệt nếu sau này thêm OAuth).

**Việc cần làm:**
- Route Handler `app/auth/callback/route.ts` xử lý `exchangeCodeForSession`.
- Nút logout gọi `supabase.auth.signOut()`, xoá session, redirect `/login`.

**Tiêu chí hoàn thành:**
- Logout xong không còn truy cập được route bảo vệ (session thật sự bị xoá, không chỉ ẩn UI).

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 1

---

### Seed role mặc định cho user đầu tiên

**Vì sao:** cần ít nhất 1 tài khoản role `owner` để test phân quyền, và có cách gán role thủ công cho tới khi có màn hình quản lý user.

**Việc cần làm:**
- Script SQL (hoặc ghi hướng dẫn trong `docs/DATABASE.md`) update `role = 'owner'` cho user đầu tiên sau khi đăng ký.
- Ghi rõ trong README cách gán role cho user tiếp theo (thủ công qua Supabase Table Editor, chưa cần UI).

**Tiêu chí hoàn thành:**
- Có 1 tài khoản `owner` dùng được để test các bước sau (Giai đoạn 2, 3).

Label: `type:chore`, `area:admin-app` · Milestone: Giai đoạn 1

## Nhóm 4 — Giai đoạn 2: Building/Room (backlog sẵn, chưa cần làm ngay)

### Migration bảng `buildings`, `rooms` + RLS

**Việc cần làm:**
- `buildings`: tên, địa chỉ, chủ sở hữu (`owner_id` → `profiles.id`).
- `rooms`: thuộc `building_id`, mã phòng, diện tích, giá thuê, trạng thái (`trong`/`dang_thue`/`bao_tri`).
- RLS: chỉ `owner`/`manager` của building đó mới đọc/sửa được room thuộc building đó.

**Tiêu chí hoàn thành:** insert thử data qua Supabase Table Editor, RLS chặn đúng user không liên quan.

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 2

---

### CRUD building

**Việc cần làm:** Server Actions hoặc Route Handlers cho tạo/sửa/xoá/list building, validate qua `zod`.

**Tiêu chí hoàn thành:** thao tác được đầy đủ CRUD từ UI, dữ liệu phản ánh đúng trong Supabase.

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 2

---

### CRUD room + đổi trạng thái

**Việc cần làm:** CRUD room gắn với building cha, thêm action riêng đổi trạng thái (trống ↔ đang thuê ↔ bảo trì) — tách khỏi update thông thường vì sẽ có business rule riêng sau này (không cho đổi "đang thuê" nếu chưa kết thúc hợp đồng, việc này liên quan Giai đoạn 3).

**Tiêu chí hoàn thành:** đổi trạng thái phòng cập nhật đúng, UI list phòng phản ánh realtime hoặc sau khi refresh.

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 2

---

### UI dashboard danh sách building/room

**Việc cần làm:** dùng HeroUI Pro dashboard/table block có sẵn, hiển thị danh sách building → drill-down xem room theo building.

**Tiêu chí hoàn thành:** xem được danh sách, lọc theo trạng thái phòng, UI khớp HeroUI Pro (không tự vẽ lại từ đầu).

Label: `type:feature`, `area:admin-app` · Milestone: Giai đoạn 2
