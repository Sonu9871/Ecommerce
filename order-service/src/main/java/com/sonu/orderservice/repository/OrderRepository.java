package com.sonu.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sonu.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
