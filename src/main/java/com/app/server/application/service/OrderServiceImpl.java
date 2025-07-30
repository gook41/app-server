package com.app.server.application.service;


import com.app.server.domain.InboundOrder;
import com.app.server.domain.OrderStatus;
import com.app.server.domain.OutboundOrder;
import com.app.server.exception.BadRequestException;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.repository.InboundOrderRepository;
import com.app.server.repository.OutboundOrderRepository;
import com.app.server.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final InboundOrderRepository inboundOrderRepository;
    private final OutboundOrderRepository outboundOrderRepository;

    public OrderServiceImpl(InboundOrderRepository inboundOrderRepository, 
                           OutboundOrderRepository outboundOrderRepository) {
        this.inboundOrderRepository = inboundOrderRepository;
        this.outboundOrderRepository = outboundOrderRepository;
    }

    // Inbound Order Operations
    @Override
    public InboundOrder saveInboundOrder(InboundOrder inboundOrder) {
        if (existsInboundOrderByOrderNumber(inboundOrder.getOrderNumber())) {
            throw new BadRequestException("Inbound order number already exists: " + inboundOrder.getOrderNumber());
        }
        return inboundOrderRepository.save(inboundOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public InboundOrder findInboundOrderById(Long id) {
        return inboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inbound order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InboundOrder> findInboundOrderByOrderNumber(String orderNumber) {
        return inboundOrderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundOrder> findAllInboundOrders() {
        return inboundOrderRepository.findAllActiveOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundOrder> findInboundOrdersByStatus(OrderStatus status) {
        return inboundOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InboundOrder> findInboundOrdersByUserId(Long userId) {
        return inboundOrderRepository.findByUserId(userId);
    }

    @Override
    public InboundOrder updateInboundOrder(Long id, InboundOrder updatedOrder) {
        InboundOrder existingOrder = findInboundOrderById(id);
        
        // Check if order number is being changed and if new order number already exists
        if (!existingOrder.getOrderNumber().equals(updatedOrder.getOrderNumber()) && 
            existsInboundOrderByOrderNumber(updatedOrder.getOrderNumber())) {
            throw new BadRequestException("Inbound order number already exists: " + updatedOrder.getOrderNumber());
        }
        
        // Update fields
        existingOrder.setOrderNumber(updatedOrder.getOrderNumber());
        existingOrder.setSupplierId(updatedOrder.getSupplierId());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalQuantity(updatedOrder.getTotalQuantity());
        existingOrder.setProcessedAt(updatedOrder.getProcessedAt());
        
        return inboundOrderRepository.save(existingOrder);
    }

    @Override
    public InboundOrder updateInboundOrderStatus(Long id, OrderStatus status) {
        InboundOrder order = findInboundOrderById(id);
        order.setStatus(status);
        
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            order.setProcessedAt(LocalDateTime.now());
        }
        
        return inboundOrderRepository.save(order);
    }

    @Override
    public void deleteInboundOrder(Long id) {
        InboundOrder order = findInboundOrderById(id);
        order.setDeleted(true);
        inboundOrderRepository.save(order);
    }

    // Outbound Order Operations
    @Override
    public OutboundOrder saveOutboundOrder(OutboundOrder outboundOrder) {
        if (existsOutboundOrderByOrderNumber(outboundOrder.getOrderNumber())) {
            throw new BadRequestException("Outbound order number already exists: " + outboundOrder.getOrderNumber());
        }
        return outboundOrderRepository.save(outboundOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OutboundOrder findOutboundOrderById(Long id) {
        return outboundOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Outbound order not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OutboundOrder> findOutboundOrderByOrderNumber(String orderNumber) {
        return outboundOrderRepository.findByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrder> findAllOutboundOrders() {
        return outboundOrderRepository.findAllActiveOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrder> findOutboundOrdersByStatus(OrderStatus status) {
        return outboundOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutboundOrder> findOutboundOrdersByUserId(Long userId) {
        return outboundOrderRepository.findByUserId(userId);
    }

    @Override
    public OutboundOrder updateOutboundOrder(Long id, OutboundOrder updatedOrder) {
        OutboundOrder existingOrder = findOutboundOrderById(id);
        
        // Check if order number is being changed and if new order number already exists
        if (!existingOrder.getOrderNumber().equals(updatedOrder.getOrderNumber()) && 
            existsOutboundOrderByOrderNumber(updatedOrder.getOrderNumber())) {
            throw new BadRequestException("Outbound order number already exists: " + updatedOrder.getOrderNumber());
        }
        
        // Update fields
        existingOrder.setOrderNumber(updatedOrder.getOrderNumber());
        existingOrder.setCustomerId(updatedOrder.getCustomerId());
        existingOrder.setStatus(updatedOrder.getStatus());
        existingOrder.setTotalQuantity(updatedOrder.getTotalQuantity());
        existingOrder.setProcessedAt(updatedOrder.getProcessedAt());
        
        return outboundOrderRepository.save(existingOrder);
    }

    @Override
    public OutboundOrder updateOutboundOrderStatus(Long id, OrderStatus status) {
        OutboundOrder order = findOutboundOrderById(id);
        order.setStatus(status);
        
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) {
            order.setProcessedAt(LocalDateTime.now());
        }
        
        return outboundOrderRepository.save(order);
    }

    @Override
    public void deleteOutboundOrder(Long id) {
        OutboundOrder order = findOutboundOrderById(id);
        order.setDeleted(true);
        outboundOrderRepository.save(order);
    }

    // Business Logic
    @Override
    @Transactional(readOnly = true)
    public boolean existsInboundOrderByOrderNumber(String orderNumber) {
        return inboundOrderRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOutboundOrderByOrderNumber(String orderNumber) {
        return outboundOrderRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public long countInboundOrdersByStatus(OrderStatus status) {
        return inboundOrderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOutboundOrdersByStatus(OrderStatus status) {
        return outboundOrderRepository.countByStatus(status);
    }

    // Order Processing
    @Override
    public InboundOrder processInboundOrder(Long id) {
        InboundOrder order = findInboundOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be processed. Current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.PROCESSING);
        return inboundOrderRepository.save(order);
    }

    @Override
    public OutboundOrder processOutboundOrder(Long id) {
        OutboundOrder order = findOutboundOrderById(id);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be processed. Current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.PROCESSING);
        return outboundOrderRepository.save(order);
    }

    @Override
    public InboundOrder cancelInboundOrder(Long id) {
        InboundOrder order = findInboundOrderById(id);
        
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed orders");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setProcessedAt(LocalDateTime.now());
        return inboundOrderRepository.save(order);
    }

    @Override
    public OutboundOrder cancelOutboundOrder(Long id) {
        OutboundOrder order = findOutboundOrderById(id);
        
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed orders");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setProcessedAt(LocalDateTime.now());
        return outboundOrderRepository.save(order);
    }
}