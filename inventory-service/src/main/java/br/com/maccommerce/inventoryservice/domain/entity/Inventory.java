package br.com.maccommerce.inventoryservice.domain.entity;

import lombok.Data;

public @Data class Inventory {

    private String id;
    private Product product;
    private String description;
    private Long amount;

}
