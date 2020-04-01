package br.com.maccommerce.inventoryservice.resources.inventory.repository;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.repository.InventoryRepository;
import br.com.maccommerce.inventoryservice.resources.inventory.entity.mapper.InventoryMapper;
import br.com.maccommerce.inventoryservice.resources.inventory.jpa.InventoryJpaRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static br.com.maccommerce.inventoryservice.resources.inventory.entity.mapper.InventoryMapper.toDomainEntity;
import static br.com.maccommerce.inventoryservice.resources.inventory.entity.mapper.InventoryMapper.toJpaEntity;

@Log4j2 @Repository public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository jpaRepository;

    @Autowired public InventoryRepositoryImpl(InventoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override public Inventory save(Inventory inventory) {
        log.info("Saving a new inventory.");
        return toDomainEntity(jpaRepository.save(toJpaEntity(inventory)));
    }

    @Override public List<Inventory> findAll() {
        log.info("Fetching all inventories.");
        return jpaRepository.findAll().stream()
                .map(InventoryMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override public Optional<Inventory> findById(String id) {
        log.info("Finding a inventory with id = {}.", id);
        return jpaRepository.findById(id)
                .map(InventoryMapper::toDomainEntity);
    }

    @Override public void delete(String id) {
        if(jpaRepository.existsById(id)) {
            log.info("Deleting a inventory with id = {}.", id);
            jpaRepository.deleteById(id);
        } else {
            log.info("Unable to delete inventory with id = {} because it was not found.", id);
        }
    }

}
