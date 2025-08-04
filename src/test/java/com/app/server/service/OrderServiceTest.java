package com.app.server.service;

import com.app.server.application.service.OrderServiceImpl;
import com.app.server.domain.*;
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
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    private InboundOrderRepository inboundOrderRepository;

    @Mock
    private OutboundOrderRepository outboundOrderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private InboundOrder testInboundOrder;
    private OutboundOrder testOutboundOrder;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testInboundOrder = new InboundOrder();
        testInboundOrder.setId(1L);
        testInboundOrder.setOrderNumber("IN001");
        testInboundOrder.setSupplierId(100L);
        testInboundOrder.setStatus(OrderStatus.PENDING);
        testInboundOrder.setTotalQuantity(50);
        testInboundOrder.setUserId(1L);
        testInboundOrder.setDeleted(false);
        testInboundOrder.setCreatedAt(now);
        testInboundOrder.setUpdatedAt(now);

        testOutboundOrder = new OutboundOrder();
        testOutboundOrder.setId(1L);
        testOutboundOrder.setOrderNumber("OUT001");
        testOutboundOrder.setCustomerId(200L);
        testOutboundOrder.setStatus(OrderStatus.PENDING);
        testOutboundOrder.setTotalQuantity(30);
        testOutboundOrder.setUserId(1L);
        testOutboundOrder.setDeleted(false);
        testOutboundOrder.setCreatedAt(now);
        testOutboundOrder.setUpdatedAt(now);
    }

    // ========== 입고 주문 테스트 ==========

    @Test
    @DisplayName("입고 주문 저장 성공 테스트")
    void saveInboundOrder_Success() {
        // given
        given(inboundOrderRepository.existsByOrderNumber("IN001")).willReturn(false);
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.saveInboundOrder(testInboundOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("IN001");
        verify(inboundOrderRepository).existsByOrderNumber("IN001");
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("중복 입고 주문 번호로 저장 시 예외 발생 테스트")
    void saveInboundOrder_DuplicateOrderNumber_ThrowsException() {
        // given
        given(inboundOrderRepository.existsByOrderNumber("IN001")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> orderService.saveInboundOrder(testInboundOrder))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Inbound order number already exists");

        verify(inboundOrderRepository).existsByOrderNumber("IN001");
        verify(inboundOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("입고 주문 ID로 조회 성공 테스트")
    void findInboundOrderById_Success() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));

        // when
        InboundOrder result = orderService.findInboundOrderById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("IN001");
        verify(inboundOrderRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 입고 주문 ID로 조회 시 예외 발생 테스트")
    void findInboundOrderById_NotFound_ThrowsException() {
        // given
        given(inboundOrderRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.findInboundOrderById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Inbound order not found with id: 999");

        verify(inboundOrderRepository).findById(999L);
    }

    @Test
    @DisplayName("주문 번호로 입고 주문 조회 테스트")
    void findInboundOrderByOrderNumber_Success() {
        // given
        given(inboundOrderRepository.findByOrderNumber("IN001")).willReturn(Optional.of(testInboundOrder));

        // when
        Optional<InboundOrder> result = orderService.findInboundOrderByOrderNumber("IN001");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("IN001");
        verify(inboundOrderRepository).findByOrderNumber("IN001");
    }

    @Test
    @DisplayName("모든 입고 주문 조회 테스트")
    void findAllInboundOrders() {
        // given
        List<InboundOrder> orders = Arrays.asList(testInboundOrder);
        given(inboundOrderRepository.findAllActiveOrderByCreatedAtDesc()).willReturn(orders);

        // when
        List<InboundOrder> result = orderService.findAllInboundOrders();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("IN001");
        verify(inboundOrderRepository).findAllActiveOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("상태별 입고 주문 조회 테스트")
    void findInboundOrdersByStatus() {
        // given
        List<InboundOrder> pendingOrders = Arrays.asList(testInboundOrder);
        given(inboundOrderRepository.findByStatus(OrderStatus.PENDING)).willReturn(pendingOrders);

        // when
        List<InboundOrder> result = orderService.findInboundOrdersByStatus(OrderStatus.PENDING);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(inboundOrderRepository).findByStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("사용자별 입고 주문 조회 테스트")
    void findInboundOrdersByUserId() {
        // given
        List<InboundOrder> userOrders = Arrays.asList(testInboundOrder);
        given(inboundOrderRepository.findByUserId(1L)).willReturn(userOrders);

        // when
        List<InboundOrder> result = orderService.findInboundOrdersByUserId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(inboundOrderRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("입고 주문 업데이트 성공 테스트")
    void updateInboundOrder_Success() {
        // given
        InboundOrder updatedOrder = new InboundOrder();
        updatedOrder.setOrderNumber("IN001_UPDATED");
        updatedOrder.setSupplierId(101L);
        updatedOrder.setStatus(OrderStatus.PROCESSING);
        updatedOrder.setTotalQuantity(60);

        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));
        given(inboundOrderRepository.existsByOrderNumber("IN001_UPDATED")).willReturn(false);
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.updateInboundOrder(1L, updatedOrder);

        // then
        assertThat(result).isNotNull();
        verify(inboundOrderRepository).findById(1L);
        verify(inboundOrderRepository).existsByOrderNumber("IN001_UPDATED");
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("입고 주문 상태 업데이트 테스트")
    void updateInboundOrderStatus_Success() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.updateInboundOrderStatus(1L, OrderStatus.PROCESSING);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(inboundOrderRepository).findById(1L);
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("입고 주문 완료 처리 시 processedAt 설정 테스트")
    void updateInboundOrderStatus_Completed_SetsProcessedAt() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.updateInboundOrderStatus(1L, OrderStatus.COMPLETED);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getProcessedAt()).isNotNull();
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("입고 주문 삭제 (Soft Delete) 테스트")
    void deleteInboundOrder() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));

        // when
        orderService.deleteInboundOrder(1L);

        // then
        assertThat(testInboundOrder.isDeleted()).isTrue();
        verify(inboundOrderRepository).findById(1L);
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("입고 주문 처리 시작 테스트")
    void processInboundOrder_Success() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.processInboundOrder(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(inboundOrderRepository).findById(1L);
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("이미 처리 중인 입고 주문 처리 시 예외 발생 테스트")
    void processInboundOrder_AlreadyProcessing_ThrowsException() {
        // given
        testInboundOrder.setStatus(OrderStatus.PROCESSING);
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));

        // when & then
        assertThatThrownBy(() -> orderService.processInboundOrder(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only pending orders can be processed");

        verify(inboundOrderRepository).findById(1L);
        verify(inboundOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("입고 주문 취소 테스트")
    void cancelInboundOrder_Success() {
        // given
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));
        given(inboundOrderRepository.save(any(InboundOrder.class))).willReturn(testInboundOrder);

        // when
        InboundOrder result = orderService.cancelInboundOrder(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(result.getProcessedAt()).isNotNull();
        verify(inboundOrderRepository).save(testInboundOrder);
    }

    @Test
    @DisplayName("완료된 입고 주문 취소 시 예외 발생 테스트")
    void cancelInboundOrder_Completed_ThrowsException() {
        // given
        testInboundOrder.setStatus(OrderStatus.COMPLETED);
        given(inboundOrderRepository.findById(1L)).willReturn(Optional.of(testInboundOrder));

        // when & then
        assertThatThrownBy(() -> orderService.cancelInboundOrder(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cannot cancel completed orders");

        verify(inboundOrderRepository, never()).save(any());
    }

    // ========== 출고 주문 테스트 ==========

    @Test
    @DisplayName("출고 주문 저장 성공 테스트")
    void saveOutboundOrder_Success() {
        // given
        given(outboundOrderRepository.existsByOrderNumber("OUT001")).willReturn(false);
        given(outboundOrderRepository.save(any(OutboundOrder.class))).willReturn(testOutboundOrder);

        // when
        OutboundOrder result = orderService.saveOutboundOrder(testOutboundOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("OUT001");
        verify(outboundOrderRepository).existsByOrderNumber("OUT001");
        verify(outboundOrderRepository).save(testOutboundOrder);
    }

    @Test
    @DisplayName("중복 출고 주문 번호로 저장 시 예외 발생 테스트")
    void saveOutboundOrder_DuplicateOrderNumber_ThrowsException() {
        // given
        given(outboundOrderRepository.existsByOrderNumber("OUT001")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> orderService.saveOutboundOrder(testOutboundOrder))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Outbound order number already exists");

        verify(outboundOrderRepository).existsByOrderNumber("OUT001");
        verify(outboundOrderRepository, never()).save(any());
    }

    @Test
    @DisplayName("출고 주문 ID로 조회 성공 테스트")
    void findOutboundOrderById_Success() {
        // given
        given(outboundOrderRepository.findById(1L)).willReturn(Optional.of(testOutboundOrder));

        // when
        OutboundOrder result = orderService.findOutboundOrderById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderNumber()).isEqualTo("OUT001");
        verify(outboundOrderRepository).findById(1L);
    }

    @Test
    @DisplayName("모든 출고 주문 조회 테스트")
    void findAllOutboundOrders() {
        // given
        List<OutboundOrder> orders = Arrays.asList(testOutboundOrder);
        given(outboundOrderRepository.findAllActiveOrderByCreatedAtDesc()).willReturn(orders);

        // when
        List<OutboundOrder> result = orderService.findAllOutboundOrders();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderNumber()).isEqualTo("OUT001");
        verify(outboundOrderRepository).findAllActiveOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("출고 주문 상태 업데이트 테스트")
    void updateOutboundOrderStatus_Success() {
        // given
        given(outboundOrderRepository.findById(1L)).willReturn(Optional.of(testOutboundOrder));
        given(outboundOrderRepository.save(any(OutboundOrder.class))).willReturn(testOutboundOrder);

        // when
        OutboundOrder result = orderService.updateOutboundOrderStatus(1L, OrderStatus.COMPLETED);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(result.getProcessedAt()).isNotNull();
        verify(outboundOrderRepository).save(testOutboundOrder);
    }

    @Test
    @DisplayName("출고 주문 처리 시작 테스트")
    void processOutboundOrder_Success() {
        // given
        given(outboundOrderRepository.findById(1L)).willReturn(Optional.of(testOutboundOrder));
        given(outboundOrderRepository.save(any(OutboundOrder.class))).willReturn(testOutboundOrder);

        // when
        OutboundOrder result = orderService.processOutboundOrder(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        verify(outboundOrderRepository).findById(1L);
        verify(outboundOrderRepository).save(testOutboundOrder);
    }

    @Test
    @DisplayName("출고 주문 취소 테스트")
    void cancelOutboundOrder_Success() {
        // given
        given(outboundOrderRepository.findById(1L)).willReturn(Optional.of(testOutboundOrder));
        given(outboundOrderRepository.save(any(OutboundOrder.class))).willReturn(testOutboundOrder);

        // when
        OutboundOrder result = orderService.cancelOutboundOrder(1L);

        // then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(result.getProcessedAt()).isNotNull();
        verify(outboundOrderRepository).save(testOutboundOrder);
    }

    // ========== 비즈니스 로직 테스트 ==========

    @Test
    @DisplayName("입고 주문 번호 존재 여부 확인 테스트")
    void existsInboundOrderByOrderNumber() {
        // given
        given(inboundOrderRepository.existsByOrderNumber("IN001")).willReturn(true);
        given(inboundOrderRepository.existsByOrderNumber("NONEXISTENT")).willReturn(false);

        // when & then
        assertThat(orderService.existsInboundOrderByOrderNumber("IN001")).isTrue();
        assertThat(orderService.existsInboundOrderByOrderNumber("NONEXISTENT")).isFalse();

        verify(inboundOrderRepository).existsByOrderNumber("IN001");
        verify(inboundOrderRepository).existsByOrderNumber("NONEXISTENT");
    }

    @Test
    @DisplayName("출고 주문 번호 존재 여부 확인 테스트")
    void existsOutboundOrderByOrderNumber() {
        // given
        given(outboundOrderRepository.existsByOrderNumber("OUT001")).willReturn(true);
        given(outboundOrderRepository.existsByOrderNumber("NONEXISTENT")).willReturn(false);

        // when & then
        assertThat(orderService.existsOutboundOrderByOrderNumber("OUT001")).isTrue();
        assertThat(orderService.existsOutboundOrderByOrderNumber("NONEXISTENT")).isFalse();

        verify(outboundOrderRepository).existsByOrderNumber("OUT001");
        verify(outboundOrderRepository).existsByOrderNumber("NONEXISTENT");
    }

    @Test
    @DisplayName("상태별 입고 주문 개수 조회 테스트")
    void countInboundOrdersByStatus() {
        // given
        given(inboundOrderRepository.countByStatus(OrderStatus.PENDING)).willReturn(5L);

        // when
        long result = orderService.countInboundOrdersByStatus(OrderStatus.PENDING);

        // then
        assertThat(result).isEqualTo(5L);
        verify(inboundOrderRepository).countByStatus(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("상태별 출고 주문 개수 조회 테스트")
    void countOutboundOrdersByStatus() {
        // given
        given(outboundOrderRepository.countByStatus(OrderStatus.COMPLETED)).willReturn(3L);

        // when
        long result = orderService.countOutboundOrdersByStatus(OrderStatus.COMPLETED);

        // then
        assertThat(result).isEqualTo(3L);
        verify(outboundOrderRepository).countByStatus(OrderStatus.COMPLETED);
    }
}
