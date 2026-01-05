package com.gateway.controllers;

import com.gateway.dto.CreateOrderRequest;
import com.gateway.dto.OrderResponseDto;
import com.gateway.dto.PublicOrderResponseDto;
import com.gateway.models.Merchant;
import com.gateway.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @RequestBody CreateOrderRequest request) {
        
        OrderResponseDto order = orderService.createOrder(apiKey, apiSecret, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @RequestHeader("X-Api-Key") String apiKey,
            @RequestHeader("X-Api-Secret") String apiSecret,
            @PathVariable String orderId) {
        
        OrderResponseDto order = orderService.getOrder(apiKey, apiSecret, orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/public")
    public ResponseEntity<PublicOrderResponseDto> getOrderPublic(
            @PathVariable String orderId) {
        
        PublicOrderResponseDto order = orderService.getOrderPublic(orderId);
        return ResponseEntity.ok(order);
    }
}
