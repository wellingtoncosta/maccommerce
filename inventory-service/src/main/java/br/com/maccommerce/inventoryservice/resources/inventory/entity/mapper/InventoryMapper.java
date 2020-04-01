package br.com.maccommerce.inventoryservice.resources.inventory.entity.mapper;

import br.com.maccommerce.inventoryservice.domain.entity.Product;

public final class InventoryMapper {

    public static br.com.maccommerce.inventoryservice.domain.entity.Inventory toDomainEntity(
            br.com.maccommerce.inventoryservice.resources.inventory.entity.Inventory entity
    ) {
        br.com.maccommerce.inventoryservice.domain.entity.Inventory domain =
                new br.com.maccommerce.inventoryservice.domain.entity.Inventory();

        Product product = new Product();
        product.setId(entity.getProductId());

        domain.setId(entity.getId());
        domain.setProduct(product);
        domain.setDescription(entity.getDescription());
        domain.setAmount(entity.getAmount());

        return domain;
    }

    public static br.com.maccommerce.inventoryservice.resources.inventory.entity.Inventory toJpaEntity(
            br.com.maccommerce.inventoryservice.domain.entity.Inventory domain
    ) {
        br.com.maccommerce.inventoryservice.resources.inventory.entity.Inventory entity =
                new br.com.maccommerce.inventoryservice.resources.inventory.entity.Inventory();

        entity.setId(domain.getId());
        entity.setProductId(domain.getProduct().getId());
        entity.setDescription(domain.getDescription());
        entity.setAmount(domain.getAmount());

        return entity;
    }

}
