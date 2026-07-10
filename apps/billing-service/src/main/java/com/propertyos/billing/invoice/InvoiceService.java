package com.propertyos.billing.invoice;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InvoiceService {

  /**
   * TODO (Giai đoạn 4 trong ROADMAP.md):
   * - Đọc contract đang ACTIVE từ Postgres (Supabase) theo billing_cycle.
   * - Lấy chỉ số điện/nước kỳ này từ MongoDB (meter_readings).
   * - Tính rent + electricity + water + service_fee -> ghi invoice + invoice_items (Postgres).
   * - Gửi email qua Resend, ghi notification_logs (MongoDB).
   */
  public Map<String, Object> generateForContract(Long contractId) {
    // Scaffold: trả về placeholder, implement dần theo roadmap.
    return Map.of(
        "contractId", contractId,
        "status", "NOT_IMPLEMENTED_YET"
    );
  }

  // Chạy 00:05 ngày 1 hàng tháng — sinh hoá đơn cho toàn bộ hợp đồng active theo chu kỳ MONTHLY.
  @Scheduled(cron = "0 5 0 1 * *")
  public void generateMonthlyInvoices() {
    // TODO: query danh sách contract active, gọi generateForContract cho từng contract.
  }
}
