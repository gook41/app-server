package com.app.server.controller;

import com.app.server.domain.*;
import com.app.server.mapper.InventoryMapper;
import com.app.server.mapper.LogMapper;
import com.app.server.mapper.OrderMapper;
import com.app.server.mapper.UserMapper;
import com.app.server.service.InventoryService;
import com.app.server.service.LogService;
import com.app.server.service.OrderService;
import com.app.server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "WMS API", description = "gook41 창고 관리 시스템 API")
public class ApiController {

    private final UserService userService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final LogService logService;
    
    private final UserMapper userMapper;
    private final InventoryMapper inventoryMapper;
    private final OrderMapper orderMapper;
    private final LogMapper logMapper;

    public ApiController(UserService userService, InventoryService inventoryService, 
                        OrderService orderService, LogService logService,
                        UserMapper userMapper, InventoryMapper inventoryMapper,
                        OrderMapper orderMapper, LogMapper logMapper) {
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.logService = logService;
        this.userMapper = userMapper;
        this.inventoryMapper = inventoryMapper;
        this.orderMapper = orderMapper;
        this.logMapper = logMapper;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/users")
    public User.Response createUser(@Valid @RequestBody User.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        User user = userMapper.toEntity(request);
        
        // 비즈니스 로직은 서비스에서 처리
        User savedUser = userService.saveUser(user);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(savedUser);
    }

    @GetMapping("/users/{id}")
    public User.Response getUserById(@PathVariable Long id) {
        // 비즈니스 로직은 서비스에서 처리
        User user = userService.findUserById(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(user);
    }

    @PutMapping("/users/{id}")
    public User.Response updateUser(@PathVariable Long id, @Valid @RequestBody User.UpdateRequest request) {
        // 기존 사용자 조회
        User existingUser = userService.findUserById(id);
        
        // UpdateRequest를 기존 엔티티에 매핑
        userMapper.updateEntity(request, existingUser);
        
        // 비즈니스 로직은 서비스에서 처리
        User updatedUser = userService.updateUser(id, existingUser);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        // 소프트 삭제 처리
        userService.deleteUser(id);
    }

    @GetMapping("/users")
    public List<User.Response> getAllUsers() {
        // 전체 사용자 목록 조회
        List<User> users = userService.findAllUsers();
        
        // MapStruct를 사용한 Entity → DTO 변환
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @GetMapping("/users/search")
    public User.Response getUserByEmail(@RequestParam String email) {
        // 이메일로 사용자 검색
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new com.app.server.exception.ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(user);
    }

    // ==================== 재고 관리 API ====================

    @GetMapping("/inventory")
    public List<Inventory.Response> getAllInventory() {
        // 전체 재고 목록 조회
        List<Inventory> inventories = inventoryService.findAllInventories();
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/inventory/{id}")
    public Inventory.Response getInventoryById(@PathVariable Long id) {
        // 재고 상세 조회
        Inventory inventory = inventoryService.findInventoryById(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inventoryMapper.toResponse(inventory);
    }

    @PostMapping("/inventory")
    public Inventory.Response createInventory(@Valid @RequestBody Inventory.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        Inventory inventory = inventoryMapper.toEntity(request);
        
        // 비즈니스 로직은 서비스에서 처리
        Inventory savedInventory = inventoryService.saveInventory(inventory);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inventoryMapper.toResponse(savedInventory);
    }

    @PutMapping("/inventory/{id}")
    public Inventory.Response updateInventory(@PathVariable Long id, @Valid @RequestBody Inventory.UpdateRequest request) {
        // 기존 재고 조회
        Inventory existingInventory = inventoryService.findInventoryById(id);
        
        // UpdateRequest를 기존 엔티티에 매핑
        inventoryMapper.updateEntity(request, existingInventory);
        
        // 비즈니스 로직은 서비스에서 처리
        Inventory updatedInventory = inventoryService.updateInventory(id, existingInventory);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inventoryMapper.toResponse(updatedInventory);
    }

    @DeleteMapping("/inventory/{id}")
    public void deleteInventory(@PathVariable Long id) {
        // 재고 삭제 처리
        inventoryService.deleteInventory(id);
    }

    @PutMapping("/inventory/{id}/quantity")
    public Inventory.Response adjustInventoryQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        // 재고 수량 조정
        Inventory adjustedInventory = inventoryService.adjustQuantity(id, quantity);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inventoryMapper.toResponse(adjustedInventory);
    }

    @GetMapping("/inventory/low-stock")
    public List<Inventory.Response> getLowStockInventory(@RequestParam(defaultValue = "10") Integer threshold) {
        // 저재고 알림 목록 조회 (기본 임계값: 10)
        List<Inventory> lowStockInventories = inventoryService.findLowStockItems(threshold);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return lowStockInventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    // ==================== 주문 관리 API ====================

    @GetMapping("/orders/inbound")
    public List<InboundOrder.Response> getAllInboundOrders() {
        // 입고 주문 목록 조회
        List<InboundOrder> inboundOrders = orderService.findAllInboundOrders();
        
        // MapStruct를 사용한 Entity → DTO 변환
        return inboundOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @GetMapping("/orders/outbound")
    public List<OutboundOrder.Response> getAllOutboundOrders() {
        // 출고 주문 목록 조회
        List<OutboundOrder> outboundOrders = orderService.findAllOutboundOrders();
        
        // MapStruct를 사용한 Entity → DTO 변환
        return outboundOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @PostMapping("/orders/inbound")
    public InboundOrder.Response createInboundOrder(@Valid @RequestBody InboundOrder.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        InboundOrder inboundOrder = orderMapper.toEntity(request);
        
        // 비즈니스 로직은 서비스에서 처리
        InboundOrder savedOrder = orderService.saveInboundOrder(inboundOrder);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(savedOrder);
    }

    @PostMapping("/orders/outbound")
    public OutboundOrder.Response createOutboundOrder(@Valid @RequestBody OutboundOrder.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        OutboundOrder outboundOrder = orderMapper.toEntity(request);
        
        // 비즈니스 로직은 서비스에서 처리
        OutboundOrder savedOrder = orderService.saveOutboundOrder(outboundOrder);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(savedOrder);
    }

    @PutMapping("/orders/inbound/{id}/status")
    public InboundOrder.Response updateInboundOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        // 입고 주문 상태 변경
        InboundOrder updatedOrder = orderService.updateInboundOrderStatus(id, status);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(updatedOrder);
    }

    @PutMapping("/orders/outbound/{id}/status")
    public OutboundOrder.Response updateOutboundOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        // 출고 주문 상태 변경
        OutboundOrder updatedOrder = orderService.updateOutboundOrderStatus(id, status);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(updatedOrder);
    }

    @PutMapping("/orders/inbound/{id}/process")
    public InboundOrder.Response processInboundOrder(@PathVariable Long id) {
        // 입고 주문 처리
        InboundOrder processedOrder = orderService.processInboundOrder(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(processedOrder);
    }

    @PutMapping("/orders/outbound/{id}/process")
    public OutboundOrder.Response processOutboundOrder(@PathVariable Long id) {
        // 출고 주문 처리
        OutboundOrder processedOrder = orderService.processOutboundOrder(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(processedOrder);
    }

    @PutMapping("/orders/inbound/{id}/cancel")
    public InboundOrder.Response cancelInboundOrder(@PathVariable Long id) {
        // 입고 주문 취소
        InboundOrder cancelledOrder = orderService.cancelInboundOrder(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(cancelledOrder);
    }

    @PutMapping("/orders/outbound/{id}/cancel")
    public OutboundOrder.Response cancelOutboundOrder(@PathVariable Long id) {
        // 출고 주문 취소
        OutboundOrder cancelledOrder = orderService.cancelOutboundOrder(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(cancelledOrder);
    }

    // ==================== 로그 조회 API ====================

    @GetMapping("/logs")
    public List<Log.Response> getAllLogs() {
        // 전체 로그 목록 조회
        List<Log> logs = logService.findAllLogs();
        
        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/logs/recent")
    public List<Log.Response> getRecentLogs(@RequestParam(defaultValue = "50") int limit) {
        // 최근 로그 조회
        List<Log> logs = logService.findRecentLogs(limit);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/logs/action/{action}")
    public List<Log.Response> getLogsByAction(@PathVariable String action) {
        // 액션별 로그 조회
        List<Log> logs = logService.findLogsByAction(action);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/logs/entity/{entityType}")
    public List<Log.Response> getLogsByEntityType(@PathVariable String entityType) {
        // 엔티티 타입별 로그 조회
        List<Log> logs = logService.findLogsByEntityType(entityType);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }

    @GetMapping("/logs/user/{userId}")
    public List<Log.Response> getLogsByUserId(@PathVariable Long userId) {
        // 특정 사용자 로그 조회
        List<Log> logs = logService.findLogsByUserId(userId);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return logs.stream()
                .map(logMapper::toResponse)
                .toList();
    }
}

