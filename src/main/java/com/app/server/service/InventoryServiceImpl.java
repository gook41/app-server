package com.app.server.service;

import com.app.server.domain.Inventory;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.exception.BadRequestException;
import com.app.server.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory saveInventory(Inventory inventory) {
        if (existsByItemCode(inventory.getItemCode())) {
            throw new BadRequestException("Item code already exists: " + inventory.getItemCode());
        }
        return inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public Inventory findInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findInventoryByItemCode(String itemCode) {
        return inventoryRepository.findByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAllInventories() {
        return inventoryRepository.findAllActiveOrderByUpdatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findInventoriesByLocation(String location) {
        return inventoryRepository.findByLocationContainingIgnoreCase(location);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findInventoriesByItemName(String itemName) {
        return inventoryRepository.findByItemNameContainingIgnoreCase(itemName);
    }

    @Override
    public Inventory updateInventory(Long id, Inventory updatedInventory) {
        Inventory existingInventory = findInventoryById(id);
        
        // Check if item code is being changed and if new item code already exists
        if (!existingInventory.getItemCode().equals(updatedInventory.getItemCode()) && 
            existsByItemCode(updatedInventory.getItemCode())) {
            throw new BadRequestException("Item code already exists: " + updatedInventory.getItemCode());
        }
        
        // Update fields
        existingInventory.setItemName(updatedInventory.getItemName());
        existingInventory.setItemCode(updatedInventory.getItemCode());
        existingInventory.setQuantity(updatedInventory.getQuantity());
        existingInventory.setLocation(updatedInventory.getLocation());
        existingInventory.setQrCode(updatedInventory.getQrCode());
        existingInventory.setUpdatedAt(LocalDateTime.now());
        
        return inventoryRepository.save(existingInventory);
    }

    @Override
    public Inventory updateQuantity(Long id, Integer quantity) {
        if (quantity < 0) {
            throw new BadRequestException("Quantity cannot be negative: " + quantity);
        }
        
        Inventory inventory = findInventoryById(id);
        inventory.setQuantity(quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory adjustQuantity(Long id, Integer adjustment) {
        Inventory inventory = findInventoryById(id);
        Integer newQuantity = inventory.getQuantity() + adjustment;
        
        if (newQuantity < 0) {
            throw new BadRequestException("Insufficient inventory. Current: " + inventory.getQuantity() + ", Adjustment: " + adjustment);
        }
        
        inventory.setQuantity(newQuantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(Long id) {
        Inventory inventory = findInventoryById(id);
        inventory.setDeleted(true);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByItemCode(String itemCode) {
        return inventoryRepository.existsByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalInventoryCount() {
        return inventoryRepository.findAllActiveOrderByUpdatedAtDesc().size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findLowStockItems(Integer threshold) {
        if (threshold < 0) {
            throw new BadRequestException("Threshold cannot be negative: " + threshold);
        }
        return inventoryRepository.findLowStockItems(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByQrCode(String qrCode) {
        return inventoryRepository.findByQrCode(qrCode)
                .map(List::of)
                .orElse(List.of());
    }
}