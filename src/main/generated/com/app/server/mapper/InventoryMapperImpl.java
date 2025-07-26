package com.app.server.mapper;

import com.app.server.domain.Inventory;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-26T20:48:44+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public Inventory.Response toResponse(Inventory inventory) {
        if ( inventory == null ) {
            return null;
        }

        Long id = null;
        String itemName = null;
        String itemCode = null;
        Integer quantity = null;
        String location = null;
        String qrCode = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        String createdBy = null;
        String updatedBy = null;
        Boolean deleted = null;

        id = inventory.getId();
        itemName = inventory.getItemName();
        itemCode = inventory.getItemCode();
        quantity = inventory.getQuantity();
        location = inventory.getLocation();
        qrCode = inventory.getQrCode();
        createdAt = inventory.getCreatedAt();
        updatedAt = inventory.getUpdatedAt();
        createdBy = inventory.getCreatedBy();
        updatedBy = inventory.getUpdatedBy();
        deleted = inventory.isDeleted();

        Inventory.Response response = new Inventory.Response( id, itemName, itemCode, quantity, location, qrCode, createdAt, updatedAt, createdBy, updatedBy, deleted );

        return response;
    }

    @Override
    public Inventory toEntity(Inventory.CreateRequest request) {
        if ( request == null ) {
            return null;
        }

        Inventory inventory = new Inventory();

        inventory.setItemName( request.itemName() );
        inventory.setItemCode( request.itemCode() );
        inventory.setQuantity( request.quantity() );
        inventory.setLocation( request.location() );
        inventory.setQrCode( request.qrCode() );

        return inventory;
    }

    @Override
    public void updateEntity(Inventory.UpdateRequest request, Inventory inventory) {
        if ( request == null ) {
            return;
        }

        inventory.setItemName( request.itemName() );
        inventory.setQuantity( request.quantity() );
        inventory.setLocation( request.location() );
    }
}
