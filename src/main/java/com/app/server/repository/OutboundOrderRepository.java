package com.app.server.repository;

import com.app.server.domain.OutboundOrder;
import com.app.server.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboundOrderRepository extends JpaRepository<OutboundOrder, Long> {

    Optional<OutboundOrder> findByOrderNumber(String orderNumber);
    
    List<OutboundOrder> findByStatus(OrderStatus status);
    
    List<OutboundOrder> findByUserId(Long userId);
    
    List<OutboundOrder> findByCustomerId(Long customerId);
    
    @Query("SELECT oo FROM OutboundOrder oo WHERE oo.status = :status AND oo.deleted = false ORDER BY oo.createdAt ASC")
    List<OutboundOrder> findActiveByStatusOrderByCreatedAt(@Param("status") OrderStatus status);
    
    @Query("SELECT oo FROM OutboundOrder oo WHERE oo.createdAt BETWEEN :startDate AND :endDate AND oo.deleted = false")
    List<OutboundOrder> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT oo FROM OutboundOrder oo WHERE oo.userId = :userId AND oo.status = :status AND oo.deleted = false")
    List<OutboundOrder> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);
    
    @Query("SELECT oo FROM OutboundOrder oo WHERE oo.deleted = false ORDER BY oo.createdAt DESC")
    List<OutboundOrder> findAllActiveOrderByCreatedAtDesc();
    
    boolean existsByOrderNumber(String orderNumber);
    
    long countByStatus(OrderStatus status);
}