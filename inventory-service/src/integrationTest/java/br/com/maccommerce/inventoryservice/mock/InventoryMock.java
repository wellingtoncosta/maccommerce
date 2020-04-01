package br.com.maccommerce.inventoryservice.mock;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.entity.Product;
import io.azam.ulidj.ULID;

public final class InventoryMock {

    public static Inventory getOne(Product product) {
        Inventory inventory = new Inventory();
        inventory.setId(ULID.random());
        inventory.setProduct(product);
        inventory.setAmount(100L);
        inventory.setDescription("Test");
        return inventory;
    }

    public static Inventory getOne() {
        Product product = new Product();
        product.setId(ULID.random());

        Inventory inventory = new Inventory();
        inventory.setId(ULID.random());
        inventory.setProduct(product);
        inventory.setAmount(100L);
        inventory.setDescription("Test");
        return inventory;
    }

}
