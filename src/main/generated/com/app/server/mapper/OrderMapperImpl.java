package com.app.server.mapper;

import com.app.server.domain.InboundOrder;
import com.app.server.domain.OrderStatus;
import com.app.server.domain.OutboundOrder;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-30T02:30:34+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public InboundOrder.Response toResponse(InboundOrder inboundOrder) {
        if ( inboundOrder == null ) {
            return null;
        }

        Long id = null;
        String orderNumber = null;
        Long supplierId = null;
        OrderStatus status = null;
        Integer totalQuantity = null;
        Long userId = null;
        LocalDateTime createdAt = null;
        LocalDateTime processedAt = null;
        LocalDateTime updatedAt = null;
        String createdBy = null;
        String updatedBy = null;
        Boolean deleted = null;

        id = inboundOrder.getId();
        orderNumber = inboundOrder.getOrderNumber();
        supplierId = inboundOrder.getSupplierId();
        status = inboundOrder.getStatus();
        totalQuantity = inboundOrder.getTotalQuantity();
        userId = inboundOrder.getUserId();
        createdAt = inboundOrder.getCreatedAt();
        processedAt = inboundOrder.getProcessedAt();
        updatedAt = inboundOrder.getUpdatedAt();
        createdBy = inboundOrder.getCreatedBy();
        updatedBy = inboundOrder.getUpdatedBy();
        deleted = inboundOrder.isDeleted();

        String userNickname = null;

        InboundOrder.Response response = new InboundOrder.Response( id, orderNumber, supplierId, status, totalQuantity, userId, userNickname, createdAt, processedAt, updatedAt, createdBy, updatedBy, deleted );

        return response;
    }

    @Override
    public InboundOrder toEntity(InboundOrder.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        InboundOrder inboundOrder = new InboundOrder();

        inboundOrder.setOrderNumber( request.orderNumber() );
        inboundOrder.setSupplierId( request.supplierId() );
        inboundOrder.setTotalQuantity( request.totalQuantity() );
        inboundOrder.setUserId( request.userId() );

        return inboundOrder;
    }

    @Override
    public void updateInboundOrder(InboundOrder.UpdateRequest request, InboundOrder inboundOrder) {
        if ( request == null ) {
            return;
        }

        inboundOrder.setStatus( request.status() );
        inboundOrder.setTotalQuantity( request.totalQuantity() );
    }

    @Override
    public OutboundOrder.Response toResponse(OutboundOrder outboundOrder) {
        if ( outboundOrder == null ) {
            return null;
        }

        Long id = null;
        String orderNumber = null;
        Long customerId = null;
        OrderStatus status = null;
        Integer totalQuantity = null;
        Long userId = null;
        LocalDateTime createdAt = null;
        LocalDateTime processedAt = null;
        LocalDateTime updatedAt = null;
        String createdBy = null;
        String updatedBy = null;
        Boolean deleted = null;

        id = outboundOrder.getId();
        orderNumber = outboundOrder.getOrderNumber();
        customerId = outboundOrder.getCustomerId();
        status = outboundOrder.getStatus();
        totalQuantity = outboundOrder.getTotalQuantity();
        userId = outboundOrder.getUserId();
        createdAt = outboundOrder.getCreatedAt();
        processedAt = outboundOrder.getProcessedAt();
        updatedAt = outboundOrder.getUpdatedAt();
        createdBy = outboundOrder.getCreatedBy();
        updatedBy = outboundOrder.getUpdatedBy();
        deleted = outboundOrder.isDeleted();

        String userNickname = null;

        OutboundOrder.Response response = new OutboundOrder.Response( id, orderNumber, customerId, status, totalQuantity, userId, userNickname, createdAt, processedAt, updatedAt, createdBy, updatedBy, deleted );

        return response;
    }

    @Override
    public OutboundOrder toEntity(OutboundOrder.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        OutboundOrder outboundOrder = new OutboundOrder();

        outboundOrder.setOrderNumber( request.orderNumber() );
        outboundOrder.setCustomerId( request.customerId() );
        outboundOrder.setTotalQuantity( request.totalQuantity() );
        outboundOrder.setUserId( request.userId() );

        return outboundOrder;
    }

    @Override
    public void updateOutboundOrder(OutboundOrder.UpdateRequest request, OutboundOrder outboundOrder) {
        if ( request == null ) {
            return;
        }

        outboundOrder.setStatus( request.status() );
        outboundOrder.setTotalQuantity( request.totalQuantity() );
    }
}
