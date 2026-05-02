package com.ws101.cunajarito.EcommerceApi.service;

import com.ws101.cunajarito.EcommerceApi.exception.ProductNotFoundException;
import com.ws101.cunajarito.EcommerceApi.model.Order;
import com.ws101.cunajarito.EcommerceApi.model.OrderItem;
import com.ws101.cunajarito.EcommerceApi.model.Product;
import com.ws101.cunajarito.EcommerceApi.repository.OrderRepository;
import com.ws101.cunajarito.EcommerceApi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Order with id " + id + " not found"));
    }

    public Order createOrder(Order order) {
        order.setOrderNumber("ORD-" + UUID.randomUUID());
        if (order.getOrderItems() != null) {
            order.getOrderItems().forEach(item -> {
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    Product product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new ProductNotFoundException(item.getProduct().getId()));
                    item.setProduct(product);
                }
                item.setOrder(order);
                item.calculateTotalPrice();
            });
            double totalAmount = order.getOrderItems().stream()
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();
            order.setTotalAmount(totalAmount);
        }
        return orderRepository.save(order);
    }
}
