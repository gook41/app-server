package com.app.server.service;

import com.app.server.application.service.InventoryServiceImpl;
import com.app.server.domain.Inventory;
import com.app.server.domain.InventoryRepository;
import com.app.server.infrastructure.exceptions.BadRequestException;
import com.app.server.infrastructure.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService 단위 테스트")
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setItemName("테스트 상품");
        testInventory.setItemCode("ITEM001");
        testInventory.setQuantity(100);
        testInventory.setLocation("A-01-01");
        testInventory.setQrCode("QR001");
        testInventory.setDeleted(false);
        testInventory.setCreatedAt(now);
        testInventory.setUpdatedAt(now);
    }

    @Test
    @DisplayName("재고 저장 성공 테스트")
    void saveInventory_Success() {
        // given
        given(inventoryRepository.existsByItemCode("ITEM001")).willReturn(false);
        given(inventoryRepository.save(any(Inventory.class))).willReturn(testInventory);

        // when
        Inventory result = inventoryService.saveInventory(testInventory);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(inventoryRepository).existsByItemCode("ITEM001");
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    @DisplayName("중복 상품 코드로 재고 저장 시 예외 발생 테스트")
    void saveInventory_DuplicateItemCode_ThrowsException() {
        // given
        given(inventoryRepository.existsByItemCode("ITEM001")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> inventoryService.saveInventory(testInventory))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Item code already exists");

        verify(inventoryRepository).existsByItemCode("ITEM001");
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("ID로 재고 조회 성공 테스트")
    void findInventoryById_Success() {
        // given
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(testInventory));

        // when
        Inventory result = inventoryService.findInventoryById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getItemCode()).isEqualTo("ITEM001");
        verify(inventoryRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 재고 조회 시 예외 발생 테스트")
    void findInventoryById_NotFound_ThrowsException() {
        // given
        given(inventoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inventoryService.findInventoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Inventory not found with id: 999");

        verify(inventoryRepository).findById(999L);
    }

    @Test
    @DisplayName("모든 재고 조회 테스트")
    void findAllInventories() {
        // given
        List<Inventory> inventories = Arrays.asList(testInventory);
        given(inventoryRepository.findAllActiveOrderByUpdatedAtDesc()).willReturn(inventories);

        // when
        List<Inventory> result = inventoryService.findAllInventories();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getItemCode()).isEqualTo("ITEM001");
        verify(inventoryRepository).findAllActiveOrderByUpdatedAtDesc();
    }

    @Test
    @DisplayName("위치별 재고 조회 테스트")
    void findInventoriesByLocation() {
        // given
        List<Inventory> inventoriesAtLocation = Arrays.asList(testInventory);
        given(inventoryRepository.findByLocationContainingIgnoreCase("A-01-01")).willReturn(inventoriesAtLocation);

        // when
        List<Inventory> result = inventoryService.findInventoriesByLocation("A-01-01");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLocation()).isEqualTo("A-01-01");
        verify(inventoryRepository).findByLocationContainingIgnoreCase("A-01-01");
    }

    @Test
    @DisplayName("재고 업데이트 성공 테스트")
    void updateInventory_Success() {
        // given
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(testInventory));
        given(inventoryRepository.save(any(Inventory.class))).willReturn(testInventory);

        // when
        Inventory result = inventoryService.updateInventory(1L, testInventory);

        // then
        assertThat(result).isNotNull();
        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    @DisplayName("재고 삭제 (Soft Delete) 테스트")
    void deleteInventory() {
        // given
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(testInventory));

        // when
        inventoryService.deleteInventory(1L);

        // then
        assertThat(testInventory.isDeleted()).isTrue();
        verify(inventoryRepository).findById(1L);
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    @DisplayName("상품 코드 존재 여부 확인 테스트")
    void existsByItemCode() {
        // given
        given(inventoryRepository.existsByItemCode("ITEM001")).willReturn(true);
        given(inventoryRepository.existsByItemCode("NONEXISTENT")).willReturn(false);

        // when & then
        assertThat(inventoryService.existsByItemCode("ITEM001")).isTrue();
        assertThat(inventoryService.existsByItemCode("NONEXISTENT")).isFalse();
        
        verify(inventoryRepository).existsByItemCode("ITEM001");
        verify(inventoryRepository).existsByItemCode("NONEXISTENT");
    }

    @Test
    @DisplayName("전체 재고 개수 조회 테스트")
    void getTotalInventoryCount() {
        // given
        List<Inventory> inventories = Arrays.asList(testInventory, testInventory, testInventory, testInventory, testInventory);
        given(inventoryRepository.findAllActiveOrderByUpdatedAtDesc()).willReturn(inventories);

        // when
        long result = inventoryService.getTotalInventoryCount();

        // then
        assertThat(result).isEqualTo(5L);
        verify(inventoryRepository).findAllActiveOrderByUpdatedAtDesc();
    }
}
