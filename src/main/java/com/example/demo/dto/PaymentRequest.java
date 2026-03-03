package com.example.demo.dto;


import java.math.BigDecimal;
import java.util.List;

public class PaymentRequest {

    private BigDecimal totalAmount;
    private List<CartItemRequest> cartItems;

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CartItemRequest> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemRequest> cartItems) {
        this.cartItems = cartItems;
    }
}