package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    // ✅ Cart count
    public int getCartItemCount(int userId) {
        return cartRepository.countTotalItems(userId);
    }


    // ✅ Add to cart
    public void addToCart(int userId, int productId, int quantity) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> existingItem =
                cartRepository.findByUserAndProduct(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem(user, product, quantity);
            cartRepository.save(newItem);
        }
    }


    // ✅ Get cart items (FULLY OPTIMIZED)
    public Map<String, Object> getCartItems(int userId) {

        List<CartItem> cartItems =
                cartRepository.findCartItemsWithProductDetails(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("role", user.getRole().toString());

        List<Map<String, Object>> products = new ArrayList<>();
        double overallTotalPrice = 0;

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            // ✅ Get images directly from product (NO EXTRA QUERY)
            List<ProductImage> images = product.getImages();

            String imageUrl = "default-image-url";

            if (images != null && !images.isEmpty()) {
                imageUrl = images.get(0).getImageUrl();
            }

            double totalPrice =
                    cartItem.getQuantity() * product.getPrice().doubleValue();

            Map<String, Object> productDetails = new HashMap<>();
            productDetails.put("product_id", product.getProductId());
            productDetails.put("image_url", imageUrl);
            productDetails.put("name", product.getName());
            productDetails.put("description", product.getDescription());
            productDetails.put("price_per_unit", product.getPrice());
            productDetails.put("quantity", cartItem.getQuantity());
            productDetails.put("total_price", totalPrice);

            products.add(productDetails);

            overallTotalPrice += totalPrice;
        }

        Map<String, Object> cart = new HashMap<>();
        cart.put("products", products);
        cart.put("overall_total_price", overallTotalPrice);

        response.put("cart", cart);

        return response;
    }


    // ✅ Update quantity
    public void updateCartItemQuantity(int userId, int productId, int quantity) {

        Optional<CartItem> existingItem =
                cartRepository.findByUserAndProduct(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();

            if (quantity <= 0) {
                cartRepository.deleteCartItem(userId, productId);
            } else {
                cartItem.setQuantity(quantity);
                cartRepository.save(cartItem);
            }
        }
    }


    // ✅ Delete item
    public void deleteCartItem(int userId, int productId) {
        cartRepository.deleteCartItem(userId, productId);
    }
}