package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentListDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("order_id")
    private String orderId;
    
    @JsonProperty("amount")
    private Integer amount;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("created_at")
    private String createdAt;

    public PaymentListDto() {}

    public PaymentListDto(String id, String orderId, Integer amount, String method, String status, String createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
