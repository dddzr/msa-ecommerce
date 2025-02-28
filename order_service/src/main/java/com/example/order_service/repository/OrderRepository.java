package com.example.order_service.repository;

import com.example.order_service.dto.request.OrderListRequest;
import com.example.order_service.entity.Orders;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Orders, Integer>, JpaSpecificationExecutor<Orders> { //<entityName, entity ID type>

    public List<Orders> findByUserId(int userId);

    public Optional<Orders> findByOrderId(int orderId);

    static Specification<Orders> filterOrders(OrderListRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), request.getUserId()));
            }
            if (request.getProductId() != null) {
                predicates.add(criteriaBuilder.equal(root.join("orderItemDetails").get("productId"), request.getProductId()));
            }
            if (!request.getStatus().equals("")) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.getStartDate().atStartOfDay()));
            }
            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.getEndDate().atTime(23, 59, 59)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    default List<Orders> findOrdersByRequest(OrderListRequest request) {
        return findAll(filterOrders(request));
    }

}
