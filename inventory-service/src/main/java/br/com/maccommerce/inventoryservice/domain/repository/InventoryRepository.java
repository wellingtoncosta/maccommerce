package br.com.maccommerce.inventoryservice.domain.repository;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {

    Inventory save(Inventory inventory);

    List<Inventory> findAll();

    Optional<Inventory> findById(String id);

    void delete(String id);

}
