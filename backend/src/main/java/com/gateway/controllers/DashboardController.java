package com.gateway.controllers;

import com.gateway.dto.PaymentListDto;
import com.gateway.dto.StatsDto;
import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.PaymentRepository;
import com.gateway.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping("/stats")
    public ResponseEntity<StatsDto> getStats(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret) {
        
        StatsDto stats = dashboardService.getStats(apiKey, apiSecret);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentListDto>> getPayments(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret) {
        
        // Authenticate merchant
        Optional<Merchant> merchantOptional = merchantRepository.findByApiKey(apiKey);
        if (merchantOptional.isEmpty() || !merchantOptional.get().getApiSecret().equals(apiSecret)) {
            throw new com.gateway.exceptions.AuthenticationException("Invalid API credentials");
        }

        Merchant merchant = merchantOptional.get();

        // Get all payments for this merchant
        List<Payment> payments = paymentRepository.findAll();
        List<PaymentListDto> result = payments.stream()
                .filter(p -> p.getMerchantId().equals(merchant.getId()))
                .map(p -> new PaymentListDto(
                        p.getId(),
                        p.getOrderId(),
                        p.getAmount(),
                        p.getMethod(),
                        p.getStatus(),
                        p.getCreatedAt().toString().replace("+00:00", "Z")
                ))
                .toList();

        return ResponseEntity.ok(result);
    }
}
