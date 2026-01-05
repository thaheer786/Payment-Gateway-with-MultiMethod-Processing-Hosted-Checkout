package com.gateway.services;

import com.gateway.dto.CreateOrderRequest;
import com.gateway.dto.OrderResponseDto;
import com.gateway.dto.PublicOrderResponseDto;
import com.gateway.exceptions.AuthenticationException;
import com.gateway.exceptions.ResourceNotFoundException;
import com.gateway.exceptions.ValidationException;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderResponseDto createOrder(String apiKey, String apiSecret, CreateOrderRequest request) {
        // Authenticate merchant
        Merchant merchant = authenticateMerchant(apiKey, apiSecret);

        // Validate request
        validateCreateOrderRequest(request);

        // Generate order ID
        String orderId = generateOrderId();

        // Create order
        Order order = new Order();
        order.setId(orderId);
        order.setMerchantId(merchant.getId());
        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "INR");
        order.setReceipt(request.getReceipt());
        
        // Convert notes to JSON string
        if (request.getNotes() != null) {
            try {
                order.setNotes(objectMapper.writeValueAsString(request.getNotes()));
            } catch (Exception e) {
                order.setNotes(null);
            }
        }
        
        order.setStatus("created");

        Order savedOrder = orderRepository.save(order);
        return mapOrderToResponse(savedOrder);
    }

    public OrderResponseDto getOrder(String apiKey, String apiSecret, String orderId) {
        // Authenticate merchant
        Merchant merchant = authenticateMerchant(apiKey, apiSecret);

        // Get order
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        Order order = orderOptional.get();
        
        // Check if merchant owns this order
        if (!order.getMerchantId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        return mapOrderToResponse(order);
    }

    public PublicOrderResponseDto getOrderPublic(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        Order order = orderOptional.get();
        return mapOrderToPublicResponse(order);
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

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request.getAmount() == null) {
            throw new ValidationException("BAD_REQUEST_ERROR", "amount is required");
        }

        if (request.getAmount() < 100) {
            throw new ValidationException("BAD_REQUEST_ERROR", "amount must be at least 100");
        }
    }

    private String generateOrderId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("order_");
        Random random = new Random();
        
        // Keep generating until we get a unique ID
        while (true) {
            sb.setLength(6); // Reset to "order_"
            for (int i = 0; i < 16; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String orderId = sb.toString();
            
            // Check if ID already exists
            if (orderRepository.findById(orderId).isEmpty()) {
                return orderId;
            }
        }
    }

    private OrderResponseDto mapOrderToResponse(Order order) {
        OrderResponseDto response = new OrderResponseDto();
        response.setId(order.getId());
        response.setMerchantId(order.getMerchantId());
        response.setAmount(order.getAmount());
        response.setCurrency(order.getCurrency());
        response.setReceipt(order.getReceipt());
        
        // Parse notes JSON
        if (order.getNotes() != null && !order.getNotes().isEmpty()) {
            try {
                response.setNotes(objectMapper.readValue(order.getNotes(), Object.class));
            } catch (Exception e) {
                response.setNotes(order.getNotes());
            }
        }
        
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt().toString().replace("+00:00", "Z"));
        response.setUpdatedAt(order.getUpdatedAt().toString().replace("+00:00", "Z"));
        
        return response;
    }

    private PublicOrderResponseDto mapOrderToPublicResponse(Order order) {
        PublicOrderResponseDto response = new PublicOrderResponseDto();
        response.setId(order.getId());
        response.setAmount(order.getAmount());
        response.setCurrency(order.getCurrency());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt().toString().replace("+00:00", "Z"));
        
        return response;
    }
}
