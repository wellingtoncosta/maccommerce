package br.com.maccommerce.inventoryservice.app.web.entity;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import lombok.Data;
import lombok.NoArgsConstructor;

public @Data @NoArgsConstructor class InventoryRegister {

    private String productId;
    private String description;
    private Long amount;

    public InventoryRegister(Inventory inventory) {
        this.productId = inventory.getProduct().getId();
        this.description = inventory.getDescription();
        this.amount = inventory.getAmount();
    }

}
