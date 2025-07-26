package com.app.server.service;

import com.app.server.domain.Log;
import java.time.LocalDateTime;
import java.util.List;

public interface LogService {
    // Create
    Log saveLog(Log log);
    Log createLog(String action, String entityType, Long entityId, Long userId, String details);
    
    // Read
    Log findLogById(Long id);
    List<Log> findAllLogs();
    List<Log> findLogsByUserId(Long userId);
    List<Log> findLogsByEntityType(String entityType);
    List<Log> findLogsByEntityId(Long entityId);
    List<Log> findLogsByAction(String action);
    List<Log> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Log> findLogsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Business logic
    long getTotalLogCount();
    List<Log> findRecentLogs(int limit);
    List<Log> findLogsByEntityTypeAndId(String entityType, Long entityId);
    
    // Audit trail methods
    void logUserAction(Long userId, String action, String entityType, Long entityId, String details);
    void logInventoryChange(Long userId, String action, Long inventoryId, String details);
    void logOrderAction(Long userId, String action, String orderType, Long orderId, String details);
    void logSystemAction(String action, String details);
}