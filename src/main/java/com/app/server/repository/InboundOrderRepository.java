package com.app.server.repository;

import com.app.server.domain.InboundOrder;
import com.app.server.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InboundOrderRepository extends JpaRepository<InboundOrder, Long> {

    Optional<InboundOrder> findByOrderNumber(String orderNumber);
    
    List<InboundOrder> findByStatus(OrderStatus status);
    
    List<InboundOrder> findByUserId(Long userId);
    
    List<InboundOrder> findBySupplierId(Long supplierId);
    
    @Query("SELECT io FROM InboundOrder io WHERE io.status = :status AND io.deleted = false ORDER BY io.createdAt ASC")
    List<InboundOrder> findActiveByStatusOrderByCreatedAt(@Param("status") OrderStatus status);
    
    @Query("SELECT io FROM InboundOrder io WHERE io.createdAt BETWEEN :startDate AND :endDate AND io.deleted = false")
    List<InboundOrder> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT io FROM InboundOrder io WHERE io.userId = :userId AND io.status = :status AND io.deleted = false")
    List<InboundOrder> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);
    
    @Query("SELECT io FROM InboundOrder io WHERE io.deleted = false ORDER BY io.createdAt DESC")
    List<InboundOrder> findAllActiveOrderByCreatedAtDesc();
}