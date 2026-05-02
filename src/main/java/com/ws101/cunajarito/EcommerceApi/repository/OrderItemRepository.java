package com.ws101.cunajarito.EcommerceApi.repository;

import com.ws101.cunajarito.EcommerceApi.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
