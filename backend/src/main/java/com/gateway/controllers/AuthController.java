package com.gateway.controllers;

import com.gateway.dto.LoginRequestDto;
import com.gateway.dto.MerchantResponseDto;
import com.gateway.exceptions.AuthenticationException;
import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private MerchantRepository merchantRepository;

    @PostMapping("/login")
    public ResponseEntity<MerchantResponseDto> login(@RequestBody LoginRequestDto request) {
        
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new AuthenticationException("Email is required");
        }

        Optional<Merchant> merchantOptional = merchantRepository.findByEmail(request.getEmail());
        if (merchantOptional.isEmpty()) {
            throw new AuthenticationException("Merchant not found");
        }

        Merchant merchant = merchantOptional.get();

        MerchantResponseDto response = new MerchantResponseDto();
        response.setEmail(merchant.getEmail());
        response.setApiKey(merchant.getApiKey());
        response.setApiSecret(merchant.getApiSecret());

        return ResponseEntity.ok(response);
    }
}
