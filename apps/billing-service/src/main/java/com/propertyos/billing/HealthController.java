package com.propertyos.billing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

  // Public — Cloud Run health check không kèm JWT
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok", "service", "billing-service");
  }
}
