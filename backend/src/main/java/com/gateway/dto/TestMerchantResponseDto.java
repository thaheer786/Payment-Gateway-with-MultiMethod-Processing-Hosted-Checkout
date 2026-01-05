package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestMerchantResponseDto {
    private String id;
    private String email;
    private String apiKey;
    private Boolean seeded;

    // Constructors
    public TestMerchantResponseDto() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getSeeded() {
        return seeded;
    }

    public void setSeeded(Boolean seeded) {
        this.seeded = seeded;
    }
}
