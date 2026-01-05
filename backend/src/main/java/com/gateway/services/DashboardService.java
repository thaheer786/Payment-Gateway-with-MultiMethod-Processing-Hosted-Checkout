package com.gateway.services;

import com.gateway.dto.StatsDto;
import com.gateway.exceptions.AuthenticationException;
import com.gateway.models.Merchant;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public StatsDto getStats(String apiKey, String apiSecret) {
        // Authenticate merchant
        Merchant merchant = authenticateMerchant(apiKey, apiSecret);

        // Get all payments for this merchant
        List<Payment> payments = paymentRepository.findAll();
        List<Payment> merchantPayments = payments.stream()
                .filter(p -> p.getMerchantId().equals(merchant.getId()))
                .toList();

        // Calculate statistics
        long totalTransactions = merchantPayments.size();
        long totalAmount = merchantPayments.stream()
                .filter(p -> "success".equals(p.getStatus()))
                .mapToLong(Payment::getAmount)
                .sum();

        long successfulPayments = merchantPayments.stream()
                .filter(p -> "success".equals(p.getStatus()))
                .count();

        double successRate = totalTransactions > 0 ? (double) successfulPayments / totalTransactions * 100 : 0;

        StatsDto stats = new StatsDto();
        stats.setTotalTransactions((int) totalTransactions);
        stats.setTotalAmount((int) totalAmount);
        stats.setSuccessRate(Math.round(successRate));

        return stats;
    }

    private Merchant authenticateMerchant(String apiKey, String apiSecret) {
        Optional<Merchant> merchantOptional = merchantRepository.findByApiKey(apiKey);
        
        if (merchantOptional.isEmpty()) {
            throw new AuthenticationException("Invalid API credentials");
        }

        Merchant merchant = merchantOptional.get();
        
        if (!merchant.getApiSecret().equals(apiSecret)) {
            throw new AuthenticationException("Invalid API credentials");
        }

        return merchant;
    }
}
