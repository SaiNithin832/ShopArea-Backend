package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    // ✅ Get cart item count
    @GetMapping("/items/count")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));

        int count = cartService.getCartItemCount(user.getUserId());

        return ResponseEntity.ok(count);
    }

    // ✅ Get all cart items
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(@RequestParam String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));

        Map<String, Object> cartItems =
                cartService.getCartItems(user.getUserId());

        return ResponseEntity.ok(cartItems);
    }

    // ✅ Add item to cart
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {

        String username = (String) request.get("username");

        int productId = ((Number) request.get("productId")).intValue();

        int quantity = request.containsKey("quantity")
                ? ((Number) request.get("quantity")).intValue()
                : 1;

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));

        cartService.addToCart(user.getUserId(), productId, quantity);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ✅ Update quantity
    @PutMapping("/update")
    public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request) {

        String username = (String) request.get("username");

        int productId = ((Number) request.get("productId")).intValue();
        int quantity = ((Number) request.get("quantity")).intValue();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));

        cartService.updateCartItemQuantity(user.getUserId(), productId, quantity);

        return ResponseEntity.ok().build();
    }

    // ✅ Delete cart item
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {

        String username = (String) request.get("username");

        int productId = ((Number) request.get("productId")).intValue();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with username: " + username));

        cartService.deleteCartItem(user.getUserId(), productId);

        return ResponseEntity.noContent().build();
    }
}