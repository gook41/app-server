package com.app.server.controller;

import com.app.server.domain.InboundOrder;
import com.app.server.domain.OrderStatus;
import com.app.server.domain.OutboundOrder;
import com.app.server.mapper.OrderMapper;
import com.app.server.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/orders")
@Tag(name = "orders API", description = "orders 관리 API")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;


    @GetMapping("/inbound")
    public List<InboundOrder.Response> getAllInboundOrders() {
        // 입고 주문 목록 조회
        List<InboundOrder> inboundOrders = orderService.findAllInboundOrders();

        // MapStruct를 사용한 Entity → DTO 변환
        return inboundOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @GetMapping("/outbound")
    public List<OutboundOrder.Response> getAllOutboundOrders() {
        // 출고 주문 목록 조회
        List<OutboundOrder> outboundOrders = orderService.findAllOutboundOrders();

        // MapStruct를 사용한 Entity → DTO 변환
        return outboundOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @PostMapping("/inbound")
    public InboundOrder.Response createInboundOrder(@Valid @RequestBody InboundOrder.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        InboundOrder inboundOrder = orderMapper.toEntity(request);

        // 비즈니스 로직은 서비스에서 처리
        InboundOrder savedOrder = orderService.saveInboundOrder(inboundOrder);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(savedOrder);
    }

    @PostMapping("/outbound")
    public OutboundOrder.Response createOutboundOrder(@Valid @RequestBody OutboundOrder.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        OutboundOrder outboundOrder = orderMapper.toEntity(request);

        // 비즈니스 로직은 서비스에서 처리
        OutboundOrder savedOrder = orderService.saveOutboundOrder(outboundOrder);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(savedOrder);
    }

    @PutMapping("/inbound/{id}/status")
    public InboundOrder.Response updateInboundOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        // 입고 주문 상태 변경
        InboundOrder updatedOrder = orderService.updateInboundOrderStatus(id, status);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(updatedOrder);
    }

    @PutMapping("/outbound/{id}/status")
    public OutboundOrder.Response updateOutboundOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        // 출고 주문 상태 변경
        OutboundOrder updatedOrder = orderService.updateOutboundOrderStatus(id, status);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(updatedOrder);
    }

    @PutMapping("/inbound/{id}/process")
    public InboundOrder.Response processInboundOrder(@PathVariable Long id) {
        // 입고 주문 처리
        InboundOrder processedOrder = orderService.processInboundOrder(id);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(processedOrder);
    }

    @PutMapping("/outbound/{id}/process")
    public OutboundOrder.Response processOutboundOrder(@PathVariable Long id) {
        // 출고 주문 처리
        OutboundOrder processedOrder = orderService.processOutboundOrder(id);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(processedOrder);
    }

    @PutMapping("/inbound/{id}/cancel")
    public InboundOrder.Response cancelInboundOrder(@PathVariable Long id) {
        // 입고 주문 취소
        InboundOrder cancelledOrder = orderService.cancelInboundOrder(id);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(cancelledOrder);
    }

    @PutMapping("/outbound/{id}/cancel")
    public OutboundOrder.Response cancelOutboundOrder(@PathVariable Long id) {
        // 출고 주문 취소
        OutboundOrder cancelledOrder = orderService.cancelOutboundOrder(id);

        // MapStruct를 사용한 Entity → DTO 변환
        return orderMapper.toResponse(cancelledOrder);
    }
}
