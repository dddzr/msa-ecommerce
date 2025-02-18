package com.example.product_service.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.product_service.event.OrderCancelEvent;
import com.example.product_service.event.OrderCreatedEvent;
import com.example.product_service.model.ProductStocks;
import com.example.product_service.model.Products;
import com.example.product_service.repository.ProductRepository;
// import com.example.product_service.repository.ProductSearchRepository;
import com.example.product_service.repository.ProductStocksRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class StockManagementService {
    private final ProductRepository productRepository;
    private final ProductStocksRepository productStocksRepository;

    // 재고 차감 (주문 생성되었을 때.)
    public void deductStock(OrderCreatedEvent orderEvent) {
        updateStock(orderEvent.getProductId(), orderEvent.getColorId(), orderEvent.getSizeId(), orderEvent.getQuantity(), "DECREASE");
    }

    // 재고 추가 (주문 취소 또는 반품 상황.)
    public void restoreStock(OrderCancelEvent orderEvent) {
        updateStock(orderEvent.getProductId(), orderEvent.getColorId(), orderEvent.getSizeId(), orderEvent.getQuantity(), "INCREASE");
    }

    // 재고 업데이트
    public void updateStock(int productId, int colorId, int sizeId, int quantity, String stockOperation) {
        // 현재 옵션에 맞는 재고 조회
        Optional<ProductStocks> optionalStock = productStocksRepository.findStockQuantity(
            productId,
            colorId,
            sizeId
        );

        if (optionalStock.isPresent()) {
            ProductStocks stock = optionalStock.get();
            int availableQuantity = stock.getStockQuantity();
            int updatedStock = availableQuantity;
            if(stockOperation.equals("INCREASE")){//추후 딴데서도 쓰면 enum 사용하도록
                updatedStock += quantity;
            }else if(stockOperation.equals("DECREASE")){
                updatedStock -= quantity;
            }

            Products products = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

            // 재고 수량 업데이트
            stock.setStockQuantity(updatedStock);
            productStocksRepository.save(stock);
            // productSearchRepository.save(productToProductDocument(products));
            System.out.println("재고 업데이트 완료");
        }else{
            System.out.println("재고 업데이트 실패");
        }
    }

    // 재고 부족 이벤트 발행
    public void publishOutOfStockEvent(int orderId, int userId, String reason){
        OrderCancelEvent event = new OrderCancelEvent();
        event.setOrderId(orderId);
        event.setUserId(userId);
        event.setReason(reason);
        
        // kafkaTemplate.send("out-of-stock-topic", event);
    }
}
