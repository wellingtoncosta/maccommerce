package br.com.maccommerce.inventoryservice.resources.inventory.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory")
public class Inventory {

    @Id
    private String id;

    @Column(nullable = false)
    private String productId;

    @Column
    private String description;

    @Column(nullable = false)
    private Long amount;

}
