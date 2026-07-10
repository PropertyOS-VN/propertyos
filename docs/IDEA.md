# Ý tưởng dự án: Hệ thống quản lý bất động sản cho thuê (PropertyOS)

## 1. Vấn đề

Chủ nhà/chủ tòa nhà đang quản lý nhiều loại tài sản cho thuê khác nhau (nhà nguyên căn, văn phòng, chung cư, chung cư mini, phòng trọ) nằm ở nhiều địa chỉ khác nhau, mỗi loại có đặc thù riêng (giá theo m², theo phòng, theo hợp đồng dài/ngắn hạn). Việc quản lý thủ công bằng Excel dẫn đến:

- Khó theo dõi tình trạng phòng/căn (trống, đang thuê, đang sửa) theo thời gian thực.
- Khó tính hóa đơn định kỳ (tiền phòng, điện, nước, dịch vụ) chính xác và đúng hạn.
- Không có lịch sử hợp đồng, thanh toán, biến động giá tập trung.
- Không gửi được nhắc nhở tự động cho khách thuê (sắp hết hạn hợp đồng, quá hạn thanh toán).

## 2. Đối tượng người dùng

- **Chủ tài sản (Owner/Landlord)**: sở hữu một hoặc nhiều tòa nhà/phòng trọ, cần dashboard tổng quan.
- **Quản lý tòa nhà (Property Manager)**: vận hành hàng ngày — tạo hợp đồng, ghi chỉ số điện nước, xuất hóa đơn.
- **Kế toán**: theo dõi công nợ, thanh toán, báo cáo doanh thu.
- **Khách thuê (Tenant)**: xem hợp đồng, hóa đơn, thanh toán online, nhận thông báo (giai đoạn mở rộng, có thể là portal riêng hoặc chỉ nhận email/Zalo/SMS ở MVP).

## 3. Phạm vi MVP (ưu tiên theo lựa chọn của bạn)

1. **Auth & User service** — đăng nhập, phân quyền theo vai trò (Owner, Manager, Accountant), quản lý user/tenant nội bộ.
2. **Building/Room management** — CRUD tòa nhà, phòng/căn, loại hình (nhà/văn phòng/chung cư/chung cư mini/phòng trọ), trạng thái phòng.
3. **Contract & Billing** — tạo hợp đồng thuê, ghi chỉ số điện nước, sinh hóa đơn định kỳ, ghi nhận thanh toán.
4. **Notification/Gateway** — API Gateway làm cổng vào duy nhất cho FE, và service gửi thông báo (email trước, SMS/Zalo OA để sau).

## 4. Phạm vi mở rộng (sau MVP)

- Portal riêng cho khách thuê (xem hóa đơn, thanh toán online qua cổng thanh toán — VNPay/Momo).
- Quản lý tài sản/thiết bị trong phòng (bảo trì, kiểm kê).
- Báo cáo/BI: doanh thu theo tòa nhà, tỷ lệ lấp đầy, dự báo dòng tiền.
- Ký hợp đồng điện tử (e-signature).
- Ứng dụng mobile cho quản lý tòa nhà đi thực địa (ghi điện nước bằng ảnh + OCR).
- Đa tổ chức (multi-tenant SaaS) nếu muốn thương mại hóa cho nhiều chủ nhà khác dùng chung hệ thống.

## 5. Mô hình dữ liệu ở mức ý tưởng

Một **Owner** sở hữu nhiều **Building**. Mỗi **Building** có nhiều **Room/Unit**. Một **Room** gắn với 0 hoặc 1 **Contract** đang active tại một thời điểm. Một **Contract** sinh ra nhiều **Invoice** theo chu kỳ (thường theo tháng). Mỗi **Invoice** có thể có nhiều **Payment** (thanh toán một phần).

Chi tiết đầy đủ xem `DATABASE.md`.
