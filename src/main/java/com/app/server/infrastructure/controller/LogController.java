package com.app.server.infrastructure.controller;

import com.app.server.application.mapper.LogMapper;
import com.app.server.domain.Log;
import com.app.server.domain.service.LogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "Log 관리 API")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;
    private final LogMapper logMapper;


    @GetMapping("/")
    public List<Log.Response> getAllLogs() {
        // 전체 로그 목록 조회
        List<Log> logs = logService.findAllLogs();

        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/recent")
    public List<Log.Response> getRecentLogs(@RequestParam(defaultValue = "50") int limit) {
        // 최근 로그 조회
        List<Log> logs = logService.findRecentLogs(limit);

        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/action/{action}")
    public List<Log.Response> getLogsByAction(@PathVariable String action) {
        // 액션별 로그 조회
        List<Log> logs = logService.findLogsByAction(action);

        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/entity/{entityType}")
    public List<Log.Response> getLogsByEntityType(@PathVariable String entityType) {
        // 엔티티 타입별 로그 조회
        List<Log> logs = logService.findLogsByEntityType(entityType);

        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/user/{userId}")
    public List<Log.Response> getLogsByUserId(@PathVariable Long userId) {
        // 특정 사용자 로그 조회
        List<Log> logs = logService.findLogsByUserId(userId);

        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }
}
