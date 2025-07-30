package com.app.server.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {

    List<Log> findByAction(String action);
    
    List<Log> findByEntityType(String entityType);
    
    List<Log> findByEntityId(Long entityId);
    
    List<Log> findByUserId(Long userId);
    
    List<Log> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    @Query("SELECT l FROM Log l WHERE l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<Log> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM Log l WHERE l.action = :action AND l.entityType = :entityType ORDER BY l.timestamp DESC")
    List<Log> findByActionAndEntityType(@Param("action") String action, @Param("entityType") String entityType);
    
    @Query("SELECT l FROM Log l WHERE l.userId = :userId AND l.timestamp BETWEEN :startDate AND :endDate ORDER BY l.timestamp DESC")
    List<Log> findByUserIdAndTimestampBetween(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT l FROM Log l WHERE l.entityType = :entityType AND l.entityId = :entityId ORDER BY l.timestamp DESC")
    List<Log> findEntityHistoryOrderByTimestampDesc(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    @Query("SELECT l FROM Log l ORDER BY l.timestamp DESC")
    List<Log> findAllOrderByTimestampDesc();
    
    @Query("SELECT DISTINCT l.action FROM Log l ORDER BY l.action")
    List<String> findDistinctActions();
    
    @Query("SELECT DISTINCT l.entityType FROM Log l ORDER BY l.entityType")
    List<String> findDistinctEntityTypes();
}