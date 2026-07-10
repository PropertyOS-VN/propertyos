package com.propertyos.billing.invoice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;

  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  public record GenerateInvoiceRequest(Long contractId) {}

  // web-app gọi endpoint này khi cần tạo hoá đơn thủ công (ngoài job định kỳ).
  @PostMapping("/generate")
  public Map<String, Object> generate(@RequestBody GenerateInvoiceRequest request) {
    return invoiceService.generateForContract(request.contractId());
  }
}
