package br.com.maccommerce.inventoryservice.domain.service;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory save(Inventory inventory);

    Inventory update(String id, Inventory inventory);

    void delete(String id);

    List<Inventory> findAll();

    Inventory findById(String id);

}
