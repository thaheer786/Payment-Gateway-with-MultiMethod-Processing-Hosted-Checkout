package com.gateway.services;

import com.gateway.dto.CreatePaymentRequest;
import com.gateway.dto.PaymentResponseDto;
import com.gateway.dto.PublicPaymentResponseDto;
import com.gateway.exceptions.AuthenticationException;
import com.gateway.exceptions.ResourceNotFoundException;
import com.gateway.exceptions.ValidationException;
import com.gateway.models.Merchant;
import com.gateway.models.Order;
import com.gateway.models.Payment;
import com.gateway.repositories.MerchantRepository;
import com.gateway.repositories.OrderRepository;
import com.gateway.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Value("${test.mode:false}")
    private Boolean testMode;

    @Value("${test.payment.success:true}")
    private Boolean testPaymentSuccess;

    @Value("${test.processing.delay:1000}")
    private Long testProcessingDelay;

    private static final Pattern VPA_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+$");
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("^[0-9]{13,19}$");
    private static final int UPI_SUCCESS_RATE = 90;
    private static final int CARD_SUCCESS_RATE = 95;
    private static final int MIN_DELAY_MS = 5000;
    private static final int MAX_DELAY_MS = 10000;

    public PaymentResponseDto createPayment(String apiKey, String apiSecret, CreatePaymentRequest request) {
        // Authenticate merchant
        Merchant merchant = authenticateMerchant(apiKey, apiSecret);

        // Validate request
        validateCreatePaymentRequest(request);

        // Get order
        Optional<Order> orderOptional = orderRepository.findById(request.getOrderId());
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        Order order = orderOptional.get();

        // Check if merchant owns this order
        if (!order.getMerchantId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Order not found");
        }

        // Generate payment ID
        String paymentId = generatePaymentId();

        // Create payment with processing status
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(order.getId());
        payment.setMerchantId(merchant.getId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(request.getMethod());
        payment.setStatus("processing"); // Always start with processing

        if ("upi".equals(request.getMethod())) {
            validateVPA(request.getVpa());
            payment.setVpa(request.getVpa());
        } else if ("card".equals(request.getMethod())) {
            validateCard(request.getCard());
            
            String cardNumber = request.getCard().getNumber();
            payment.setCardLast4(cardNumber.substring(cardNumber.length() - 4));
            payment.setCardNetwork(detectCardNetwork(cardNumber));
        }

        // Save payment with processing status
        Payment savedPayment = paymentRepository.save(payment);

        // Process payment asynchronously
        processPaymentAsync(savedPayment, request.getMethod());

        // Return response immediately with processing status
        return mapPaymentToResponse(savedPayment);
    }

    public PaymentResponseDto getPayment(String apiKey, String apiSecret, String paymentId) {
        // Authenticate merchant
        Merchant merchant = authenticateMerchant(apiKey, apiSecret);

        // Get payment
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isEmpty()) {
            throw new ResourceNotFoundException("Payment not found");
        }

        Payment payment = paymentOptional.get();

        // Check if merchant owns this payment
        if (!payment.getMerchantId().equals(merchant.getId())) {
            throw new ResourceNotFoundException("Payment not found");
        }

        return mapPaymentToResponse(payment);
    }

    public PublicPaymentResponseDto createPaymentPublic(CreatePaymentRequest request) {
        // Validate request
        validateCreatePaymentRequest(request);

        // Get order
        Optional<Order> orderOptional = orderRepository.findById(request.getOrderId());
        if (orderOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        Order order = orderOptional.get();

        // Get merchant for this order
        Optional<Merchant> merchantOptional = merchantRepository.findById(order.getMerchantId());
        if (merchantOptional.isEmpty()) {
            throw new ResourceNotFoundException("Order not found");
        }

        Merchant merchant = merchantOptional.get();

        // Generate payment ID
        String paymentId = generatePaymentId();

        // Create payment with processing status
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setOrderId(order.getId());
        payment.setMerchantId(merchant.getId());
        payment.setAmount(order.getAmount());
        payment.setCurrency(order.getCurrency());
        payment.setMethod(request.getMethod());
        payment.setStatus("processing"); // Always start with processing

        if ("upi".equals(request.getMethod())) {
            validateVPA(request.getVpa());
            payment.setVpa(request.getVpa());
        } else if ("card".equals(request.getMethod())) {
            validateCard(request.getCard());
            
            String cardNumber = request.getCard().getNumber();
            payment.setCardLast4(cardNumber.substring(cardNumber.length() - 4));
            payment.setCardNetwork(detectCardNetwork(cardNumber));
        }

        // Save payment with processing status
        Payment savedPayment = paymentRepository.save(payment);

        // Process payment asynchronously
        processPaymentAsync(savedPayment, request.getMethod());

        // Return response immediately with processing status
        return mapPaymentToPublicResponse(savedPayment);
    }

    public PublicPaymentResponseDto getPaymentPublic(String paymentId) {
        // Get payment
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        if (paymentOptional.isEmpty()) {
            throw new ResourceNotFoundException("Payment not found");
        }

        Payment payment = paymentOptional.get();
        return mapPaymentToPublicResponse(payment);
    }

    @Async
    public void processPaymentAsync(Payment payment, String method) {
        try {
            // Simulate bank processing delay
            long delayMs = getProcessingDelay();
            Thread.sleep(delayMs);

            // Determine success/failure
            boolean isSuccess = determinePaymentSuccess(method);

            // Update payment status
            if (isSuccess) {
                payment.setStatus("success");
            } else {
                payment.setStatus("failed");
                payment.setErrorCode("PAYMENT_FAILED");
                payment.setErrorDescription("Payment declined by bank");
            }

            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.setStatus("failed");
            payment.setErrorCode("PAYMENT_FAILED");
            payment.setErrorDescription("Payment processing interrupted");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }
    }

    private long getProcessingDelay() {
        if (testMode != null && testMode) {
            return testProcessingDelay != null ? testProcessingDelay : 1000L;
        }
        Random random = new Random();
        return MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS + 1);
    }

    private boolean determinePaymentSuccess(String method) {
        if (testMode != null && testMode) {
            return testPaymentSuccess != null ? testPaymentSuccess : true;
        }

        Random random = new Random();
        int randomValue = random.nextInt(100);
        int successRate = "upi".equals(method) ? UPI_SUCCESS_RATE : CARD_SUCCESS_RATE;

        return randomValue < successRate;
    }

    private Merchant authenticateMerchant(String apiKey, String apiSecret) {
        Optional<Merchant> merchantOptional = merchantRepository.findByApiKey(apiKey);
        
        if (merchantOptional.isEmpty()) {
            throw new AuthenticationException("Invalid API credentials");
        }

        Merchant merchant = merchantOptional.get();
        
        if (!merchant.getApiSecret().equals(apiSecret)) {
            throw new AuthenticationException("Invalid API credentials");
        }

        return merchant;
    }

    private void validateCreatePaymentRequest(CreatePaymentRequest request) {
        if (request.getOrderId() == null || request.getOrderId().isEmpty()) {
            throw new ValidationException("BAD_REQUEST_ERROR", "order_id is required");
        }

        if (request.getMethod() == null || request.getMethod().isEmpty()) {
            throw new ValidationException("BAD_REQUEST_ERROR", "method is required");
        }

        if (!request.getMethod().equals("upi") && !request.getMethod().equals("card")) {
            throw new ValidationException("BAD_REQUEST_ERROR", "method must be either 'upi' or 'card'");
        }

        if ("upi".equals(request.getMethod())) {
            if (request.getVpa() == null || request.getVpa().isEmpty()) {
                throw new ValidationException("BAD_REQUEST_ERROR", "vpa is required for UPI payments");
            }
        } else if ("card".equals(request.getMethod())) {
            if (request.getCard() == null) {
                throw new ValidationException("BAD_REQUEST_ERROR", "card details are required for card payments");
            }
        }
    }

    private void validateVPA(String vpa) {
        if (!VPA_PATTERN.matcher(vpa).matches()) {
            throw new ValidationException("INVALID_VPA", "VPA format is invalid");
        }
    }

    private void validateCard(CreatePaymentRequest.CardDetails card) {
        if (card.getNumber() == null || !CARD_NUMBER_PATTERN.matcher(card.getNumber()).matches()) {
            throw new ValidationException("INVALID_CARD", "Card number is invalid");
        }

        // Validate using Luhn algorithm
        if (!isValidCardNumber(card.getNumber())) {
            throw new ValidationException("INVALID_CARD", "Card number is invalid");
        }

        if (card.getExpiry_month() == null || card.getExpiry_year() == null) {
            throw new ValidationException("EXPIRED_CARD", "Card expiry date is invalid");
        }

        if (!isValidExpiryDate(card.getExpiry_month(), card.getExpiry_year())) {
            throw new ValidationException("EXPIRED_CARD", "Card expiry date is invalid");
        }

        if (card.getCvv() == null || !card.getCvv().matches("^[0-9]{3,4}$")) {
            throw new ValidationException("INVALID_CARD", "CVV is invalid");
        }
    }

    private boolean isValidCardNumber(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s+|-", "");

        if (!cleaned.matches("^[0-9]{13,19}$")) {
            return false;
        }

        // Luhn algorithm
        int sum = 0;
        boolean alternate = false;

        for (int i = cleaned.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cleaned.charAt(i));

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

    private boolean isValidExpiryDate(String expiryMonth, String expiryYear) {
        try {
            int month = Integer.parseInt(expiryMonth);
            int year = Integer.parseInt(expiryYear);

            if (month < 1 || month > 12) {
                return false;
            }

            // Handle 2-digit year format
            if (expiryYear.length() == 2) {
                year = 2000 + year;
            }

            LocalDateTime expiryDate = LocalDateTime.of(year, month, 1, 23, 59, 59);
            LocalDateTime now = LocalDateTime.now();

            return expiryDate.isAfter(now) || expiryDate.toLocalDate().isEqual(now.toLocalDate());

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String detectCardNetwork(String cardNumber) {
        String cleaned = cardNumber.replaceAll("\\s+", "");

        if (cleaned.startsWith("4")) {
            return "visa";
        } else if (cleaned.matches("^5[1-5].*")) {
            return "mastercard";
        } else if (cleaned.matches("^3[47].*")) {
            return "amex";
        } else if (cleaned.matches("^(60|65|8[1-9]).*")) {
            return "rupay";
        }

        return "unknown";
    }

    private String generatePaymentId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder("pay_");
        Random random = new Random();

        while (true) {
            sb.setLength(4); // Reset to "pay_"
            for (int i = 0; i < 16; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String paymentId = sb.toString();

            if (paymentRepository.findById(paymentId).isEmpty()) {
                return paymentId;
            }
        }
    }

    private PaymentResponseDto mapPaymentToResponse(Payment payment) {
        PaymentResponseDto response = new PaymentResponseDto();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());

        if ("upi".equals(payment.getMethod())) {
            response.setVpa(payment.getVpa());
        } else if ("card".equals(payment.getMethod())) {
            response.setCardNetwork(payment.getCardNetwork());
            response.setCardLast4(payment.getCardLast4());
        }

        if (payment.getErrorCode() != null) {
            response.setErrorCode(payment.getErrorCode());
        }
        if (payment.getErrorDescription() != null) {
            response.setErrorDescription(payment.getErrorDescription());
        }

        response.setCreatedAt(payment.getCreatedAt().toString().replace("+00:00", "Z"));
        response.setUpdatedAt(payment.getUpdatedAt().toString().replace("+00:00", "Z"));

        return response;
    }

    private PublicPaymentResponseDto mapPaymentToPublicResponse(Payment payment) {
        PublicPaymentResponseDto response = new PublicPaymentResponseDto();
        response.setId(payment.getId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setMethod(payment.getMethod());
        response.setStatus(payment.getStatus());

        if (payment.getErrorCode() != null) {
            response.setErrorCode(payment.getErrorCode());
        }
        if (payment.getErrorDescription() != null) {
            response.setErrorDescription(payment.getErrorDescription());
        }

        response.setCreatedAt(payment.getCreatedAt().toString().replace("+00:00", "Z"));
        response.setUpdatedAt(payment.getUpdatedAt().toString().replace("+00:00", "Z"));

        return response;
    }
}
