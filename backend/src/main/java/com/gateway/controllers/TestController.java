package com.gateway.controllers;

import com.gateway.dto.TestMerchantResponseDto;
import com.gateway.exceptions.ResourceNotFoundException;
import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @Autowired
    private MerchantRepository merchantRepository;

    private static final UUID TEST_MERCHANT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @GetMapping("/merchant")
    public ResponseEntity<TestMerchantResponseDto> getTestMerchant() {
        Optional<Merchant> merchantOptional = merchantRepository.findById(TEST_MERCHANT_ID);
        
        if (merchantOptional.isEmpty()) {
            throw new ResourceNotFoundException("Test merchant not found");
        }

        Merchant merchant = merchantOptional.get();
        
        TestMerchantResponseDto response = new TestMerchantResponseDto();
        response.setId(merchant.getId().toString());
        response.setEmail(merchant.getEmail());
        response.setApiKey(merchant.getApiKey());
        response.setSeeded(true);
        
        return ResponseEntity.ok(response);
    }
}
