package com.example.order_service.repository;

import com.example.order_service.entity.Orders;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Integer> { //<entityName, entity ID type>

    public List<Orders> findByUserId(int userId);

    public Optional<Orders> findByOrderId(int orderId);

}
