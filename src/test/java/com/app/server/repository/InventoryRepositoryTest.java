package com.app.server.repository;

import com.app.server.domain.Inventory;
import com.app.server.domain.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InventoryRepository 통합 테스트")
class InventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    private Inventory testInventory1;
    private Inventory testInventory2;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testInventory1 = new Inventory();
        testInventory1.setItemName("테스트 상품 1");
        testInventory1.setItemCode("ITEM001");
        testInventory1.setQuantity(100);
        testInventory1.setLocation("A-01-01");
        testInventory1.setQrCode("QR001");
        testInventory1.setDeleted(false);
        testInventory1.setCreatedBy("system");
        testInventory1.setUpdatedBy("system");
        testInventory1.setCreatedAt(now);
        testInventory1.setUpdatedAt(now);

        testInventory2 = new Inventory();
        testInventory2.setItemName("테스트 상품 2");
        testInventory2.setItemCode("ITEM002");
        testInventory2.setQuantity(50);
        testInventory2.setLocation("A-01-02");
        testInventory2.setQrCode("QR002");
        testInventory2.setDeleted(false);
        testInventory2.setCreatedBy("system");
        testInventory2.setUpdatedBy("system");
        testInventory2.setCreatedAt(now);
        testInventory2.setUpdatedAt(now);
    }

    @Test
    @DisplayName("재고 저장 및 조회 테스트")
    void saveAndFindInventory() {
        // given
        Inventory savedInventory = inventoryRepository.save(testInventory1);
        entityManager.flush();
        entityManager.clear();

        // when
        Optional<Inventory> foundInventory = inventoryRepository.findById(savedInventory.getId());

        // then
        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getItemName()).isEqualTo("테스트 상품 1");
        assertThat(foundInventory.get().getItemCode()).isEqualTo("ITEM001");
        assertThat(foundInventory.get().getQuantity()).isEqualTo(100);
        assertThat(foundInventory.get().getLocation()).isEqualTo("A-01-01");
    }

    @Test
    @DisplayName("상품 코드로 재고 조회 테스트")
    void findByItemCode() {
        // given
        inventoryRepository.save(testInventory1);
        entityManager.flush();

        // when
        Optional<Inventory> foundInventory = inventoryRepository.findByItemCode("ITEM001");

        // then
        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getItemName()).isEqualTo("테스트 상품 1");
    }

    @Test
    @DisplayName("상품 코드 존재 여부 확인 테스트")
    void existsByItemCode() {
        // given
        inventoryRepository.save(testInventory1);
        entityManager.flush();

        // when & then
        assertThat(inventoryRepository.existsByItemCode("ITEM001")).isTrue();
        assertThat(inventoryRepository.existsByItemCode("NONEXISTENT")).isFalse();
    }

    @Test
    @DisplayName("QR 코드로 재고 조회 테스트")
    void findByQrCode() {
        // given
        inventoryRepository.save(testInventory1);
        entityManager.flush();

        // when
        Optional<Inventory> foundInventory = inventoryRepository.findByQrCode("QR001");

        // then
        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getItemCode()).isEqualTo("ITEM001");
    }

    @Test
    @DisplayName("위치별 재고 조회 테스트")
    void findByLocationContainingIgnoreCase() {
        // given
        inventoryRepository.save(testInventory1);
        inventoryRepository.save(testInventory2);
        entityManager.flush();

        // when
        List<Inventory> inventoriesAtLocation = inventoryRepository.findByLocationContainingIgnoreCase("A-01");

        // then
        assertThat(inventoriesAtLocation).hasSize(2);
    }

    @Test
    @DisplayName("상품명으로 재고 검색 테스트")
    void findByItemNameContainingIgnoreCase() {
        // given
        inventoryRepository.save(testInventory1);
        inventoryRepository.save(testInventory2);
        entityManager.flush();

        // when
        List<Inventory> inventories = inventoryRepository.findByItemNameContainingIgnoreCase("테스트");

        // then
        assertThat(inventories).hasSize(2);
    }

    @Test
    @DisplayName("활성 재고 목록 조회 테스트")
    void findAllActiveOrderByUpdatedAtDesc() {
        // given
        inventoryRepository.save(testInventory1);
        inventoryRepository.save(testInventory2);
        entityManager.flush();

        // when
        List<Inventory> activeInventories = inventoryRepository.findAllActiveOrderByUpdatedAtDesc();

        // then
        assertThat(activeInventories).hasSize(2);
        assertThat(activeInventories)
                .extracting(Inventory::getItemCode)
                .containsExactlyInAnyOrder("ITEM001", "ITEM002");
    }

    @Test
    @DisplayName("재고 부족 상품 조회 테스트")
    void findLowStockItems() {
        // given
        testInventory1.setQuantity(5); // 낮은 재고
        testInventory2.setQuantity(100); // 충분한 재고
        inventoryRepository.save(testInventory1);
        inventoryRepository.save(testInventory2);
        entityManager.flush();

        // when
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems(10);

        // then
        assertThat(lowStockItems).hasSize(1);
        assertThat(lowStockItems.get(0).getItemCode()).isEqualTo("ITEM001");
    }
}
