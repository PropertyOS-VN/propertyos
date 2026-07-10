# Luồng làm việc: Claude + Cursor

Nguyên tắc phân vai: **Claude nghĩ, Cursor viết**. Claude không sửa code trực tiếp trong flow chuẩn — Claude ra plan/spec/review bằng văn bản (markdown), người thực thi (dùng Cursor) đọc và implement. Điều này giữ 1 "nguồn sự thật" cho quyết định thiết kế (nằm trong `docs/` hoặc GitHub Issue), tránh tình trạng 2 người dùng 2 AI khác nhau hiểu sai ý nhau qua lời kể.

## Bảng phân việc

| Việc | Công cụ | Vì sao |
|---|---|---|
| Lên ý tưởng, phạm vi tính năng | Claude | Cần hỏi-đáp, cân nhắc trade-off, không cần thấy code |
| Thiết kế kiến trúc/DB (thêm bảng, đổi luồng service) | Claude | Ảnh hưởng nhiều file/nhiều repo, cần nhìn toàn cục — xem `docs/ARCHITECTURE.md`, `docs/DATABASE.md` |
| Viết spec/plan cho 1 task cụ thể | Claude | Output là markdown, dùng skill `write-spec` nếu cần format PRD đầy đủ |
| Viết code theo spec | Cursor | Cursor thấy trực tiếp file/dự án, autocomplete, chạy/debug local nhanh |
| Review code trước khi merge | Claude | Đọc toàn bộ diff, đối chiếu với spec + `AGENTS.md`/conventions, không bị thiên vị vì không phải người viết |
| Sửa theo review, fix bug | Cursor | Vòng lặp nhanh, chạy test/debug tại chỗ |
| Refactor lớn (đổi kiến trúc, đổi thư viện) | Claude đề xuất → Cursor thực thi | Claude đánh giá rủi ro/phạm vi ảnh hưởng trước, tránh Cursor tự ý đổi khi chưa lường hết tác động |

## Vòng đời 1 task (6 bước)

1. **Tạo issue** trên GitHub (repo tương ứng: `propertyos-admin-app` hoặc `propertyos-billing-service`, hoặc `propertyos` nếu ảnh hưởng cả 2). Mô tả ngắn gọn vấn đề/tính năng.
2. **Claude lên plan** — dán nội dung issue vào Claude, yêu cầu viết plan: mục tiêu, phạm vi (làm gì/không làm gì), các bước implement theo thứ tự, file/module sẽ đụng tới, edge case cần test. Dán plan này làm comment vào issue (hoặc lưu `docs/specs/<ten-task>.md` nếu là thay đổi lớn ảnh hưởng kiến trúc).
3. **Tạo branch**, implement bằng Cursor theo đúng plan — Cursor tự đọc `AGENTS.md`/`CLAUDE.md`/`.cursor/rules` của repo đó để theo đúng convention (đã setup sẵn cho cả `admin-app` và `billing-service`).
4. **Mở PR**, dán link PR (hoặc diff) cho Claude review. Yêu cầu Claude: (a) đối chiếu code với plan ban đầu — có làm đúng phạm vi không, (b) rà theo checklist review bên dưới, (c) liệt kê điểm cần sửa, xếp theo mức độ (bắt buộc sửa / nên sửa / góp ý thêm).
5. **Cursor sửa theo review** — không cần hỏi lại Claude từng điểm nhỏ, chỉ quay lại hỏi nếu Claude review có điểm mơ hồ hoặc phát sinh vấn đề mới ngoài plan ban đầu.
6. **Merge** khi review không còn điểm bắt buộc sửa. Nếu task có thay đổi submodule (`admin-app`/`billing-service`), nhớ quay lại repo gốc `propertyos` cập nhật con trỏ submodule (xem `AGENTS.md` root).

## Checklist Claude dùng khi review

- Có đúng theo plan/spec đã thống nhất không — có phần nào code làm khác hoặc thiếu so với plan?
- Có vi phạm convention trong `AGENTS.md`/`.cursor/rules` của repo đó không (VD: field injection thay vì constructor injection ở `billing-service`, gọi Postgres trực tiếp thay vì qua Supabase client ở `admin-app`)?
- Bug logic, edge case chưa xử lý (đặc biệt: tính toán tài chính ở `billing-service` — làm tròn số, hợp đồng hết hạn giữa kỳ, thanh toán một phần).
- Bảo mật: endpoint mới có yêu cầu authenticated đúng chưa, RLS có bật cho bảng Supabase mới không, có secret nào bị hardcode/commit nhầm không.
- Performance: N+1 query, gọi API lặp không cần thiết giữa `admin-app` và `billing-service`.
- Test: có test cho phần logic quan trọng chưa (đặc biệt các case tài chính ở `billing-service`).

Có thể dùng thẳng skill `code-review` (Claude) cho bước này thay vì tự liệt kê lại checklist mỗi lần.

## Khi nào KHÔNG theo flow đầy đủ này

Việc nhỏ (sửa text, đổi màu, fix typo, bump 1 dependency không breaking) — Cursor làm thẳng, không cần Claude lên plan/review riêng. Flow đầy đủ dành cho: tính năng mới, đổi schema DB, đổi luồng nghiệp vụ tài chính, refactor ảnh hưởng nhiều file.

## Liên quan

- Quy ước code từng phần: `apps/admin-app/AGENTS.md`, `apps/billing-service/AGENTS.md` (đọc trước khi implement).
- Kiến trúc/DB tổng thể: `docs/ARCHITECTURE.md`, `docs/DATABASE.md` — cập nhật file này khi Claude ra quyết định thiết kế mới, đừng để plan chỉ nằm trong lịch sử chat.
- Quản lý task: xem gợi ý GitHub Projects trong README (bảng Board/Table/Roadmap theo `docs/ROADMAP.md`).
