package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequestDto {
    
    @JsonProperty("email")
    private String email;

    public LoginRequestDto() {}

    public LoginRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
