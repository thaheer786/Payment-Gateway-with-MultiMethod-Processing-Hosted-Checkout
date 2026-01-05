package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatsDto {
    
    @JsonProperty("total_transactions")
    private int totalTransactions;
    
    @JsonProperty("total_amount")
    private int totalAmount;
    
    @JsonProperty("success_rate")
    private long successRate;

    public StatsDto() {}

    public StatsDto(int totalTransactions, int totalAmount, long successRate) {
        this.totalTransactions = totalTransactions;
        this.totalAmount = totalAmount;
        this.successRate = successRate;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(long successRate) {
        this.successRate = successRate;
    }
}
