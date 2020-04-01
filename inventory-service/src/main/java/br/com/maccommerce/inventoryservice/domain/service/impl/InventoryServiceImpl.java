package br.com.maccommerce.inventoryservice.domain.service.impl;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.exception.InventoryNotFoundException;
import br.com.maccommerce.inventoryservice.domain.exception.ProductNotFoundException;
import br.com.maccommerce.inventoryservice.domain.repository.InventoryRepository;
import br.com.maccommerce.inventoryservice.domain.repository.ProductRepository;
import br.com.maccommerce.inventoryservice.domain.service.InventoryService;
import io.azam.ulidj.ULID;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2 @Service public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override @Transactional public Inventory save(Inventory inventory) {
        return productRepository.findById(inventory.getProduct().getId())
                .map(product -> {
                    inventory.setId(ULID.random());
                    Inventory saved = inventoryRepository.save(inventory);
                    saved.setProduct(product);
                    return saved;
                })
                .orElseThrow(() -> {
                    log.error(
                            "Unable to save inventory because was not possible to get product for id = {}.",
                            inventory.getProduct().getId()
                    );
                    return new ProductNotFoundException(inventory.getProduct().getId());
                });
    }

    @Override @Transactional public Inventory update(String id, Inventory inventory) {
        return inventoryRepository.findById(id)
                .map(inventoryFromDb -> {
                    inventory.setId(id);
                    return productRepository.findById(inventory.getProduct().getId())
                            .map(product -> {
                                Inventory updated = inventoryRepository.save(inventory);
                                updated.setProduct(product);
                                return updated;
                            })
                            .orElseThrow(() -> {
                                log.error(
                                        "Unable to save inventory because was not possible to get product for id = {}.",
                                        inventory.getProduct().getId()
                                );
                                return new ProductNotFoundException(inventory.getProduct().getId());
                            });
                })
                .orElseThrow(() -> {
                    log.error("Inventory with id = {} was not found.", id);
                    return new InventoryNotFoundException(id);
                });
    }

    @Override @Transactional public void delete(String id) {
        inventoryRepository.delete(id);
    }

    @Override @Transactional(readOnly = true) public List<Inventory> findAll() {
        return inventoryRepository.findAll()
                .stream().map(this::findAndSetProduct)
                .collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true) public Inventory findById(String id) {
        return inventoryRepository.findById(id)
                .map(this::findAndSetProduct)
                .orElseThrow(() -> {
                    log.error("Inventory with id = {} was not found.", id);
                    return new InventoryNotFoundException(id);
                });
    }

    private Inventory findAndSetProduct(Inventory inventory) {
        productRepository.findById(inventory.getProduct().getId())
                .ifPresent(inventory::setProduct);

        return inventory;
    }

}
