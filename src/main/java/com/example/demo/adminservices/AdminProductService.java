package com.example.demo.adminservices;



import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;

    public AdminProductService(ProductRepository productRepository,
            ProductImageRepository productImageRepository,
            CategoryRepository categoryRepository,
            CartItemRepository cartItemRepository) {

this.productRepository = productRepository;
this.productImageRepository = productImageRepository;
this.categoryRepository = categoryRepository;
this.cartItemRepository = cartItemRepository;
}

    public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl) {
        // Validate the category
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Invalid category ID");
        }

        // Create and save the product
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setCategory(category.get());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        // Create and save the product image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setImageUrl(imageUrl);
            productImageRepository.save(productImage);
        } else {
            throw new IllegalArgumentException("Product image URL cannot be empty");
        }

        return savedProduct;
    }
    
    public void deleteProduct(Integer productId) {

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Product not found");
        }

        // 🔥 DELETE CART ITEMS FIRST
        cartItemRepository.deleteByProduct_ProductId(productId);

        // Delete product images
        productImageRepository.deleteByProductId(productId);

        // Finally delete product
        productRepository.deleteById(productId);
    }
}