package br.com.maccommerce.inventoryservice.resources.inventory.jpa;

import br.com.maccommerce.inventoryservice.resources.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface InventoryJpaRepository extends JpaRepository<Inventory, String> { }
