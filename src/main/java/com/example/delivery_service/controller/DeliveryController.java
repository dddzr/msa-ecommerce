package com.example.delivery_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.delivery_service.model.Deliveries;
import com.example.delivery_service.service.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }
    @GetMapping("/{deliveryId}")
    public ResponseEntity<Deliveries> getDeliveriesByDeliveryId(@PathVariable int deliveryId) {
        Deliveries Delivery = deliveryService.getDeliveryByDeliveryId(deliveryId);
        return ResponseEntity.ok(Delivery);
    }
}
