package com.gateway.config;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private MerchantRepository merchantRepository;

    @Value("${test.merchant.id}")
    private String testMerchantId;

    @Value("${test.merchant.name}")
    private String testMerchantName;

    @Value("${test.merchant.email}")
    private String testMerchantEmail;

    @Value("${test.merchant.api-key}")
    private String testMerchantApiKey;

    @Value("${test.merchant.api-secret}")
    private String testMerchantApiSecret;

    @Override
    public void run(String... args) throws Exception {
        // Check if test merchant already exists
        if (merchantRepository.findByEmail(testMerchantEmail).isEmpty()) {
            Merchant testMerchant = new Merchant();
            testMerchant.setId(UUID.fromString(testMerchantId));
            testMerchant.setName(testMerchantName);
            testMerchant.setEmail(testMerchantEmail);
            testMerchant.setApiKey(testMerchantApiKey);
            testMerchant.setApiSecret(testMerchantApiSecret);
            testMerchant.setIsActive(true);

            merchantRepository.save(testMerchant);
            System.out.println("✓ Test merchant created successfully");
            System.out.println("  ID: " + testMerchantId);
            System.out.println("  Email: " + testMerchantEmail);
            System.out.println("  API Key: " + testMerchantApiKey);
        } else {
            System.out.println("✓ Test merchant already exists");
        }
    }
}
