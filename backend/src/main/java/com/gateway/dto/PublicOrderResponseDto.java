package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicOrderResponseDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("amount")
    private Integer amount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("created_at")
    private String createdAt;

    public PublicOrderResponseDto() {}

    public PublicOrderResponseDto(String id, Integer amount, String currency, String status, String createdAt) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
