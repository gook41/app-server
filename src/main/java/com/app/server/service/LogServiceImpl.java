package com.app.server.service;

import com.app.server.domain.Log;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.repository.LogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public Log saveLog(Log log) {
        if (log.getTimestamp() == null) {
            log.setTimestamp(LocalDateTime.now());
        }
        return logRepository.save(log);
    }

    @Override
    public Log createLog(String action, String entityType, Long entityId, Long userId, String details) {
        Log log = new Log();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setUserId(userId);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        
        return logRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Log findLogById(Long id) {
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Log not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findAllLogs() {
        return logRepository.findAllOrderByTimestampDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByUserId(Long userId) {
        return logRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByEntityType(String entityType) {
        return logRepository.findByEntityType(entityType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByEntityId(Long entityId) {
        return logRepository.findByEntityId(entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByAction(String action) {
        return logRepository.findByAction(action);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByTimestampBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByUserIdAndTimestampBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalLogCount() {
        return logRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findRecentLogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return logRepository.findAllOrderByTimestampDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Log> findLogsByEntityTypeAndId(String entityType, Long entityId) {
        return logRepository.findEntityHistoryOrderByTimestampDesc(entityType, entityId);
    }

    // Audit trail methods
    @Override
    public void logUserAction(Long userId, String action, String entityType, Long entityId, String details) {
        createLog(action, entityType, entityId, userId, details);
    }

    @Override
    public void logInventoryChange(Long userId, String action, Long inventoryId, String details) {
        createLog(action, "Inventory", inventoryId, userId, details);
    }

    @Override
    public void logOrderAction(Long userId, String action, String orderType, Long orderId, String details) {
        createLog(action, orderType, orderId, userId, details);
    }

    @Override
    public void logSystemAction(String action, String details) {
        createLog(action, "System", null, null, details);
    }
}