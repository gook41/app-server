package com.app.server.domain.service;


import com.app.server.domain.InboundOrder;
import com.app.server.domain.OutboundOrder;
import com.app.server.domain.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    
    // Inbound Order Operations
    InboundOrder saveInboundOrder(InboundOrder inboundOrder);
    InboundOrder findInboundOrderById(Long id);
    Optional<InboundOrder> findInboundOrderByOrderNumber(String orderNumber);
    List<InboundOrder> findAllInboundOrders();
    List<InboundOrder> findInboundOrdersByStatus(OrderStatus status);
    List<InboundOrder> findInboundOrdersByUserId(Long userId);
    InboundOrder updateInboundOrder(Long id, InboundOrder inboundOrder);
    InboundOrder updateInboundOrderStatus(Long id, OrderStatus status);
    void deleteInboundOrder(Long id);
    
    // Outbound Order Operations
    OutboundOrder saveOutboundOrder(OutboundOrder outboundOrder);
    OutboundOrder findOutboundOrderById(Long id);
    Optional<OutboundOrder> findOutboundOrderByOrderNumber(String orderNumber);
    List<OutboundOrder> findAllOutboundOrders();
    List<OutboundOrder> findOutboundOrdersByStatus(OrderStatus status);
    List<OutboundOrder> findOutboundOrdersByUserId(Long userId);
    OutboundOrder updateOutboundOrder(Long id, OutboundOrder outboundOrder);
    OutboundOrder updateOutboundOrderStatus(Long id, OrderStatus status);
    void deleteOutboundOrder(Long id);
    
    // Business Logic
    boolean existsInboundOrderByOrderNumber(String orderNumber);
    boolean existsOutboundOrderByOrderNumber(String orderNumber);
    long countInboundOrdersByStatus(OrderStatus status);
    long countOutboundOrdersByStatus(OrderStatus status);
    
    // Order Processing
    InboundOrder processInboundOrder(Long id);
    OutboundOrder processOutboundOrder(Long id);
    InboundOrder cancelInboundOrder(Long id);
    OutboundOrder cancelOutboundOrder(Long id);
}