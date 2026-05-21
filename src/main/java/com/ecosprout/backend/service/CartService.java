package com.ecosprout.backend.service;

import com.ecosprout.backend.model.Order;
import com.ecosprout.backend.model.OrderItem;
import com.ecosprout.backend.model.Product;
import com.ecosprout.backend.model.User;
import com.ecosprout.backend.repository.OrderItemRepository;
import com.ecosprout.backend.repository.OrderRepository;
import com.ecosprout.backend.repository.ProductRepository;
import com.ecosprout.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    private Order getOrCreateCart(User user) {
        return orderRepository.findByUserAndStatus(user, "In Cart")
                .orElseGet(() -> {
                    Order newCart = new Order();
                    newCart.setUser(user);
                    newCart.setStatus("In Cart");
                    newCart.setOrderDate(Instant.now());
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return orderRepository.save(newCart);
                });
    }

    public Order addProductToCart(String username, Long productId, int quantityToAdd) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        Order cart = getOrCreateCart(user);

        // --- STOCK CHECK LOGIC ---
        int availableStock = product.getStockQuantity();
        if (availableStock <= 0) {
            throw new RuntimeException("Sorry, this product is out of stock.");
        }

        Optional<OrderItem> existingItemOpt = orderItemRepository.findByOrderAndProduct(cart, product);

        int currentQuantityInCart = 0;
        if (existingItemOpt.isPresent()) {
            currentQuantityInCart = existingItemOpt.get().getQuantity();
        }

        if (currentQuantityInCart + quantityToAdd > availableStock) {
            throw new RuntimeException("Not enough stock available. Only " + (availableStock - currentQuantityInCart) + " more can be added.");
        }
        // --- END STOCK CHECK ---

        if (existingItemOpt.isPresent()) {
            OrderItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantityToAdd);
            orderItemRepository.save(item);
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setOrder(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantityToAdd);
            newItem.setUnitPrice(product.getPrice()); // Price at the time of purchase
            cart.getOrderItems().add(newItem);
            orderItemRepository.save(newItem);
        }

        updateCartTotal(cart);
        return orderRepository.save(cart);
    }


    public Order removeProductFromCart(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Order cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        Optional<OrderItem> existingItem = orderItemRepository.findByOrderAndProduct(cart, product);

        if (existingItem.isPresent()) {
            OrderItem itemToRemove = existingItem.get();
            cart.getOrderItems().remove(itemToRemove);
            orderItemRepository.delete(itemToRemove);

            updateCartTotal(cart);
            return orderRepository.save(cart);
        }

        return cart;
    }


    public Order updateCartItemQuantity(String username, Long productId, int newQuantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Order cart = getOrCreateCart(user);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));


        if (newQuantity <= 0) {
            return removeProductFromCart(username, productId);
        }

        if (newQuantity > product.getStockQuantity()) {
            throw new RuntimeException("Not enough stock. Only " + product.getStockQuantity() + " available.");
        }


        OrderItem item = orderItemRepository.findByOrderAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart."));

        item.setQuantity(newQuantity);
        orderItemRepository.save(item);

        updateCartTotal(cart);
        return orderRepository.save(cart);
    }

    private void updateCartTotal(Order cart) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : cart.getOrderItems()) {
            total = total.add(item.getUnitPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        cart.setTotalAmount(total);
    }

    public Order checkoutCart(String username, Map<String, String> shippingDetails) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Order cart = getOrCreateCart(user);

        if (cart.getOrderItems().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart.");
        }


        for (OrderItem item : cart.getOrderItems()) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getStockQuantity()) {
                throw new RuntimeException("Checkout failed: Product '" + product.getName() + "' does not have enough stock.");
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }


        cart.setShippingAddressLine1(shippingDetails.get("addressLine1"));
        cart.setShippingAddressLine2(shippingDetails.get("addressLine2"));
        cart.setShippingCity(shippingDetails.get("city"));
        cart.setShippingPostalCode(shippingDetails.get("postalCode"));
        cart.setShippingCountry(shippingDetails.get("country"));

        cart.setStatus("Processing");
        cart.setOrderDate(Instant.now());

        return orderRepository.save(cart);
    }
}