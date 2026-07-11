---
name: spring-boot-conventions
description: Quy ước code Spring Boot cho billing-service của PropertyOS — kiến trúc theo domain package, DTO/Entity tách biệt, transaction cho thao tác tài chính, verify JWT Supabase, kết nối Postgres (Supabase) + MongoDB Atlas, deploy Render. Dùng khi tạo/sửa code trong repo propertyos-billing-service.
---

# Spring Boot conventions — billing-service (PropertyOS)

Repo này (`propertyos-billing-service`) là submodule của repo gốc `propertyos`. Skill này dùng khi được yêu cầu thêm entity/API/job mới, refactor, viết test, hoặc chuẩn bị deploy Render cho service này.

## 1. Kiến trúc: domain package, không phải layer phẳng

Tổ chức theo domain (giống `invoice/` đã có), KHÔNG tạo `controller/`, `service/`, `repository/` ở cấp root:

```
src/main/java/com/propertyos/billing/
├── BillingServiceApplication.java
├── HealthController.java
├── config/SecurityConfig.java
├── invoice/
│   ├── InvoiceController.java
│   ├── InvoiceService.java
│   ├── InvoiceRepository.java      (JPA — khi thêm entity thật)
│   ├── Invoice.java                 (Entity)
│   └── dto/
│       ├── GenerateInvoiceRequest.java
│       └── InvoiceResponse.java
├── contract/     (khi cần đọc contract từ Postgres)
├── payment/      (khi thêm API ghi nhận thanh toán)
└── meterreading/ (Mongo document + repository)
```

Mỗi domain package tự chứa entity/document, repository, service, controller, DTO của riêng nó.

## 2. Entity vs DTO — luôn tách biệt

- Entity (`@Entity`) chỉ dùng nội bộ, KHÔNG bao giờ trả thẳng ra JSON response.
- Request/response dùng Java `record`, đặt trong package con `dto/` của domain tương ứng.
- Map Entity ↔ DTO thủ công trong Service (project nhỏ, chưa cần MapStruct — chỉ thêm MapStruct nếu số field lớn và mapping lặp lại nhiều).

```java
public record InvoiceResponse(Long id, String period, BigDecimal totalAmount, String status) {
  public static InvoiceResponse from(Invoice invoice) {
    return new InvoiceResponse(invoice.getId(), invoice.getPeriod(), invoice.getTotalAmount(), invoice.getStatus());
  }
}
```

## 3. Dependency injection

Luôn dùng constructor injection (implicit qua `final` field + 1 constructor, hoặc Lombok `@RequiredArgsConstructor`). Không dùng `@Autowired` trên field.

```java
@Service
@RequiredArgsConstructor
public class InvoiceService {
  private final InvoiceRepository invoiceRepository;
  private final MeterReadingRepository meterReadingRepository; // Mongo
}
```

## 4. Transaction cho thao tác tài chính

Mọi flow ghi invoice + invoice_items + cập nhật trạng thái phải nằm trong 1 `@Transactional` ở tầng Service (không phải Controller):

```java
@Transactional
public InvoiceResponse generateForContract(Long contractId) {
  // đọc contract, đọc meter_readings (Mongo), tính toán, save Invoice + InvoiceItem trong cùng transaction
}
```

Không gọi nhiều `repository.save()` rời rạc ở Controller rồi rollback thủ công.

## 5. Bảo mật — verify JWT Supabase

`SecurityConfig` đã cấu hình `oauth2ResourceServer().jwt()` trỏ tới JWKS của Supabase. Khi thêm endpoint mới:

- Mặc định yêu cầu authenticated (không cần khai báo gì thêm).
- Chỉ thêm vào danh sách `permitAll()` trong `SecurityConfig` nếu endpoint thực sự public (VD: `/health`).
- Lấy thông tin user từ token: inject `JwtAuthenticationToken` hoặc `@AuthenticationPrincipal Jwt jwt` vào controller method, đọc claim `sub` (user id Supabase) khi cần biết ai gọi API.

## 6. Data access — Postgres (Supabase) vs MongoDB Atlas

| Loại dữ liệu | Dùng gì |
|---|---|
| invoices, invoice_items, payments, contracts (đọc), tenants (đọc) | Spring Data JPA (Postgres qua Supabase) |
| meter_readings, room_attributes (đọc), notification_logs | Spring Data MongoDB (Atlas) |

Không lưu dữ liệu tài chính vào Mongo, không lưu log/dữ liệu linh hoạt vào Postgres.

## 7. Job định kỳ

Dùng `@Scheduled` (đã bật `@EnableScheduling` ở `BillingServiceApplication`). Cron viết theo giờ Việt Nam trong comment kèm giá trị UTC nếu server chạy timezone khác — container trên Render mặc định chạy UTC, nên nếu muốn "00:05 giờ VN ngày 1 hàng tháng" thì cron phải tính lệch +7h hoặc set biến môi trường `TZ=Asia/Ho_Chi_Minh` ở Render.

## 8. Deploy Render

- Đọc port từ `${PORT:8082}` (đã cấu hình) — không hardcode, Render cũng tự set `PORT` giống Cloud Run.
- Test image local trước khi deploy: `docker build -t billing-service . && docker run -p 8082:8080 -e PORT=8080 billing-service`.
- Deploy qua Render Dashboard (New → Web Service → Runtime: Docker → Instance Type: Free), không cần CLI.
- Biến môi trường nhạy cảm (SUPABASE_DB_PASSWORD, MONGODB_ATLAS_URI...) set ở Render Dashboard > service > Environment, không commit vào repo.
- Free tier Render sleep sau 15 phút không traffic, cold start ~30-50s — chấp nhận được vì không cần thẻ tín dụng, không có rủi ro phát sinh phí.

## 9. Test

- JUnit 5 + Mockito, file test cạnh class trong `src/test/java/...` cùng package.
- Mock `InvoiceRepository`/Mongo repository trong unit test Service — không kết nối DB thật.
- Ưu tiên test case: sinh hóa đơn đúng hạn, hợp đồng hết hạn giữa kỳ, thanh toán một phần, thanh toán đủ.

## 10. Comment tiếng Việt

Comment/Javadoc tiếng Việt phải gõ có dấu đầy đủ (VD: "Sinh hoá đơn cho hợp đồng đang active" — không viết "Sinh hoa don cho hop dong dang active"). Không dùng tiếng Việt không dấu trong code.

## Áp dụng cho Cursor

Nội dung skill này cũng nên được Cursor đọc — xem `.cursor/rules/backend-spring-boot.mdc` trong repo này, file đó tóm tắt lại các quy tắc trên theo format Cursor Rules.
