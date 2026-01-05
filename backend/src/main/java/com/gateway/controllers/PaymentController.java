package com.gateway.controllers;

import com.gateway.dto.CreatePaymentRequest;
import com.gateway.dto.PaymentResponseDto;
import com.gateway.dto.PublicPaymentResponseDto;
import com.gateway.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @RequestBody CreatePaymentRequest request) {
        
        PaymentResponseDto payment = paymentService.createPayment(apiKey, apiSecret, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @PathVariable String paymentId) {
        
        PaymentResponseDto payment = paymentService.getPayment(apiKey, apiSecret, paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/public")
    public ResponseEntity<PublicPaymentResponseDto> createPaymentPublic(
            @RequestBody CreatePaymentRequest request) {
        
        PublicPaymentResponseDto payment = paymentService.createPaymentPublic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/{paymentId}/public")
    public ResponseEntity<PublicPaymentResponseDto> getPaymentPublic(
            @PathVariable String paymentId) {
        
        PublicPaymentResponseDto payment = paymentService.getPaymentPublic(paymentId);
        return ResponseEntity.ok(payment);
    }
}
