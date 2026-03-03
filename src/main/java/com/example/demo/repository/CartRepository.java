package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartItem;

import jakarta.transaction.Transactional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {

    // Fetch specific cart item
    @Query("""
        SELECT c 
        FROM CartItem c 
        WHERE c.user.userId = :userId 
        AND c.product.productId = :productId
    """)
    Optional<CartItem> findByUserAndProduct(int userId, int productId);


    // ✅ FETCH CART + PRODUCT + PRODUCT IMAGES (FIXED)
    @Query("""
        SELECT DISTINCT c
        FROM CartItem c
        JOIN FETCH c.product p
        LEFT JOIN FETCH p.images
        WHERE c.user.userId = :userId
    """)
    List<CartItem> findCartItemsWithProductDetails(int userId);


    // Update quantity
    @Modifying
    @Transactional
    @Query("""
        UPDATE CartItem c 
        SET c.quantity = :quantity 
        WHERE c.id = :cartItemId
    """)
    void updateCartItemQuantity(int cartItemId, int quantity);


    // Delete single item
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM CartItem c 
        WHERE c.user.userId = :userId 
        AND c.product.productId = :productId
    """)
    void deleteCartItem(int userId, int productId);


    // Count total quantity
    @Query("""
        SELECT COALESCE(SUM(c.quantity), 0) 
        FROM CartItem c 
        WHERE c.user.userId = :userId
    """)
    int countTotalItems(int userId);


    // Delete all items
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM CartItem c 
        WHERE c.user.userId = :userId
    """)
    void deleteAllCartItemsByUserId(int userId);
}