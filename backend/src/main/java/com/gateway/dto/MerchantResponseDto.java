package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MerchantResponseDto {
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("api_key")
    private String apiKey;
    
    @JsonProperty("api_secret")
    private String apiSecret;

    public MerchantResponseDto() {}

    public MerchantResponseDto(String email, String apiKey, String apiSecret) {
        this.email = email;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
