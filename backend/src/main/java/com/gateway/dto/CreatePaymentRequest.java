package com.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreatePaymentRequest {
    private String orderId;
    private String method; // "upi" or "card"
    private String vpa; // For UPI
    private CardDetails card; // For Card

    public static class CardDetails {
        private String number;
        private String expiry_month;
        private String expiry_year;
        private String cvv;
        private String holder_name;

        // Getters and Setters
        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getExpiry_month() {
            return expiry_month;
        }

        public void setExpiry_month(String expiry_month) {
            this.expiry_month = expiry_month;
        }

        public String getExpiry_year() {
            return expiry_year;
        }

        public void setExpiry_year(String expiry_year) {
            this.expiry_year = expiry_year;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public String getHolder_name() {
            return holder_name;
        }

        public void setHolder_name(String holder_name) {
            this.holder_name = holder_name;
        }
    }

    // Constructors
    public CreatePaymentRequest() {}

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVpa() {
        return vpa;
    }

    public void setVpa(String vpa) {
        this.vpa = vpa;
    }

    public CardDetails getCard() {
        return card;
    }

    public void setCard(CardDetails card) {
        this.card = card;
    }
}
