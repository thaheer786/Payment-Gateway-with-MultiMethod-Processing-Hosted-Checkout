package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublicPaymentResponseDto {
    
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
    
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("error_description")
    private String errorDescription;
    
    @JsonProperty("created_at")
    private String createdAt;
    
    @JsonProperty("updated_at")
    private String updatedAt;

    public PublicPaymentResponseDto() {}

    public PublicPaymentResponseDto(String id, String orderId, Integer amount, String method, String status,
                                    String errorCode, String errorDescription, String createdAt, String updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
