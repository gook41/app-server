package com.app.server.repository;

import com.app.server.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByItemCode(String itemCode);
    
    Optional<Inventory> findByQrCode(String qrCode);
    
    List<Inventory> findByLocationContainingIgnoreCase(String location);
    
    List<Inventory> findByItemNameContainingIgnoreCase(String itemName);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= :threshold AND i.deleted = false")
    List<Inventory> findLowStockItems(@Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM Inventory i WHERE i.deleted = false ORDER BY i.updatedAt DESC")
    List<Inventory> findAllActiveOrderByUpdatedAtDesc();
    
    boolean existsByItemCode(String itemCode);
}