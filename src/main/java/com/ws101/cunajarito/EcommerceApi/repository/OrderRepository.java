package com.ws101.cunajarito.EcommerceApi.repository;

import com.ws101.cunajarito.EcommerceApi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
