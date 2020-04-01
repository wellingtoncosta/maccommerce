package br.com.maccommerce.inventoryservice.domain.exception;

public class ProductNotFoundException extends ApiException {

    public ProductNotFoundException(String id) {
        super(
                ApiExceptionType.NOT_FOUND,
                "Product with id = " + id + " was not found."
        );
    }

}
