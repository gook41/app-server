package com.app.server.service;

import com.app.server.domain.Inventory;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    // Create
    Inventory saveInventory(Inventory inventory);
    
    // Read
    Inventory findInventoryById(Long id);
    Optional<Inventory> findInventoryByItemCode(String itemCode);
    List<Inventory> findAllInventories();
    List<Inventory> findInventoriesByLocation(String location);
    List<Inventory> findInventoriesByItemName(String itemName);
    
    // Update
    Inventory updateInventory(Long id, Inventory inventory);
    Inventory updateQuantity(Long id, Integer quantity);
    Inventory adjustQuantity(Long id, Integer adjustment); // +/- adjustment
    
    // Delete
    void deleteInventory(Long id);
    
    // Business logic
    boolean existsByItemCode(String itemCode);
    long getTotalInventoryCount();
    List<Inventory> findLowStockItems(Integer threshold);
    List<Inventory> findByQrCode(String qrCode);
}