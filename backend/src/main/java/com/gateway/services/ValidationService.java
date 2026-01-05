package com.gateway.services;

import com.gateway.dto.OrderRequest;
import com.gateway.dto.PaymentRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile(
        "^[0-9]{13,19}$"
    );

    private static final Pattern CVV_PATTERN = Pattern.compile(
        "^[0-9]{3,4}$"
    );

    public void validateOrderRequest(OrderRequest orderRequest) {
        if (orderRequest.getMerchantId() == null || orderRequest.getMerchantId().isEmpty()) {
            throw new IllegalArgumentException("Merchant ID is required");
        }

        if (orderRequest.getAmount() == null || orderRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid amount is required");
        }

        if (orderRequest.getCurrency() == null || orderRequest.getCurrency().length() != 3) {
            throw new IllegalArgumentException("Valid currency code is required");
        }

        if (orderRequest.getCustomerEmail() != null && !isValidEmail(orderRequest.getCustomerEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public void validatePaymentRequest(PaymentRequest paymentRequest) {
        if (paymentRequest.getOrderId() == null || paymentRequest.getOrderId().isEmpty()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        if (paymentRequest.getAmount() == null || paymentRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valid amount is required");
        }

        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().length() != 3) {
            throw new IllegalArgumentException("Valid currency code is required");
        }

        if (paymentRequest.getPaymentMethod() == null || paymentRequest.getPaymentMethod().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        // Validate card details if payment method is card
        if ("card".equals(paymentRequest.getPaymentMethod())) {
            validateCardDetails(
                paymentRequest.getCardNumber(),
                paymentRequest.getCardExpiry(),
                paymentRequest.getCardCvv()
            );
        }

        if (paymentRequest.getCustomerEmail() != null && !isValidEmail(paymentRequest.getCustomerEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public void validateCardDetails(String cardNumber, String expiry, String cvv) {
        if (cardNumber == null || !isValidCardNumber(cardNumber)) {
            throw new IllegalArgumentException("Invalid card number");
        }

        if (expiry == null || !isValidExpiry(expiry)) {
            throw new IllegalArgumentException("Invalid card expiry");
        }

        if (cvv == null || !isValidCVV(cvv)) {
            throw new IllegalArgumentException("Invalid CVV");
        }
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        String cleaned = cardNumber.replaceAll("\\s+", "");
        return CARD_NUMBER_PATTERN.matcher(cleaned).matches() && luhnCheck(cleaned);
    }

    public boolean isValidExpiry(String expiry) {
        if (expiry == null) {
            return false;
        }
        
        String cleaned = expiry.replaceAll("[^0-9]", "");
        if (cleaned.length() != 4) {
            return false;
        }

        int month = Integer.parseInt(cleaned.substring(0, 2));
        int year = Integer.parseInt(cleaned.substring(2, 4));
        
        return month >= 1 && month <= 12 && year >= 0;
    }

    public boolean isValidCVV(String cvv) {
        return cvv != null && CVV_PATTERN.matcher(cvv).matches();
    }

    // Luhn algorithm for card number validation
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            
            sum += digit;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
}
