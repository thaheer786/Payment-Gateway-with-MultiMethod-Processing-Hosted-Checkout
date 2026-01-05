package com.gateway.dto;

import com.gateway.models.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private String orderId;
    private String merchantId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private String customerEmail;
    private LocalDateTime createdAt;

    // Constructors
    public OrderResponse() {}

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.merchantId = order.getMerchantId() != null ? order.getMerchantId().toString() : null;
        this.amount = order.getAmount() != null ? BigDecimal.valueOf(order.getAmount()) : null;
        this.currency = order.getCurrency();
        this.status = order.getStatus();
        this.description = null; // Field not present in Order model
        this.customerEmail = null; // Field not present in Order model
        this.createdAt = order.getCreatedAt();
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
