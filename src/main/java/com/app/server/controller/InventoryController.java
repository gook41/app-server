package com.app.server.controller;

import com.app.server.domain.Inventory;
import com.app.server.mapper.InventoryMapper;
import com.app.server.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "inventory API", description = "inventory 관리 API")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @GetMapping("/")
    public List<Inventory.Response> getAllInventory() {
        // 전체 재고 목록 조회
        List<Inventory> inventories = inventoryService.findAllInventories();

        // MapStruct를 사용한 Entity → DTO 변환
        return inventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        // 재고 삭제 처리
        inventoryService.deleteInventory(id);
    }

    @PutMapping("/{id}/quantity")
    public Inventory.Response adjustInventoryQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        // 재고 수량 조정
        Inventory adjustedInventory = inventoryService.adjustQuantity(id, quantity);

        // MapStruct를 사용한 Entity → DTO 변환
        return inventoryMapper.toResponse(adjustedInventory);
    }

    @GetMapping("/low-stock")
    public List<Inventory.Response> getLowStockInventory(@RequestParam(defaultValue = "10") Integer threshold) {
        // 저재고 알림 목록 조회 (기본 임계값: 10)
        List<Inventory> lowStockInventories = inventoryService.findLowStockItems(threshold);

        // MapStruct를 사용한 Entity → DTO 변환
        return lowStockInventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }
}
