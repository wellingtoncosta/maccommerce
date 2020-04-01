package br.com.maccommerce.inventoryservice.domain.exception;

public class InventoryNotFoundException extends ApiException {

    public InventoryNotFoundException(String id) {
        super(
                ApiExceptionType.NOT_FOUND,
                "Inventory with id = " + id + " was not found."
        );
    }

}
