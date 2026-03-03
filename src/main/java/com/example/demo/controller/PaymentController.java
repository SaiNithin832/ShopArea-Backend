package com.example.demo.controller;

import com.example.demo.dto.CartItemRequest;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.service.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Create Razorpay Order
     */
    @PostMapping("/create")
    public ResponseEntity<String> createPaymentOrder(
            @RequestBody PaymentRequest requestBody,
            HttpServletRequest request) {

        try {

            // ✅ Get authenticated user from filter
            User user = (User) request.getAttribute("authenticatedUser");

            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            // ✅ Get total amount
            BigDecimal totalAmount = requestBody.getTotalAmount();

            if (totalAmount == null) {
                return ResponseEntity
                        .badRequest()
                        .body("Total amount is required");
            }

            // ✅ Null-safe cart items
            List<CartItemRequest> cartItemsRaw =
                    Optional.ofNullable(requestBody.getCartItems())
                            .orElse(List.of());

            if (cartItemsRaw.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body("Cart is empty");
            }

            // ✅ Convert DTO → OrderItem entity
            List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {

                OrderItem orderItem = new OrderItem();

                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPricePerUnit(item.getPrice());

                orderItem.setTotalPrice(
                        item.getPrice().multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                );

                return orderItem;

            }).collect(Collectors.toList());

            // ✅ Call service
            String razorpayOrderId =
                    paymentService.createOrder(
                            user.getUserId(),
                            totalAmount,
                            cartItems
                    );

            return ResponseEntity.ok(razorpayOrderId);

        } catch (RazorpayException e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Razorpay order: " + e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body("Invalid request data: " + e.getMessage());
        }
    }

    /**
     * Verify Razorpay Payment
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody java.util.Map<String, Object> requestBody,
            HttpServletRequest request) {

        try {

            User user = (User) request.getAttribute("authenticatedUser");

            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            String razorpayOrderId =
                    (String) requestBody.get("razorpayOrderId");

            String razorpayPaymentId =
                    (String) requestBody.get("razorpayPaymentId");

            String razorpaySignature =
                    (String) requestBody.get("razorpaySignature");

            boolean isVerified =
                    paymentService.verifyPayment(
                            razorpayOrderId,
                            razorpayPaymentId,
                            razorpaySignature,
                            user.getUserId()
                    );

            if (isVerified) {
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity
                        .badRequest()
                        .body("Payment verification failed");
            }

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }
}