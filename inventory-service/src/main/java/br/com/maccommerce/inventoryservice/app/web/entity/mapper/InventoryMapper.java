package br.com.maccommerce.inventoryservice.app.web.entity.mapper;

import br.com.maccommerce.inventoryservice.app.web.entity.InventoryRegister;
import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.entity.Product;

public final class InventoryMapper {

    public static Inventory toDomainEntity(InventoryRegister register) {
        Inventory inventory = new Inventory();
        Product product = new Product();

        product.setId(register.getProductId());
        inventory.setDescription(register.getDescription());
        inventory.setAmount(register.getAmount());
        inventory.setProduct(product);

        return inventory;
    }

}
