package br.com.maccommerce.inventoryservice.app.web.controller;

import br.com.maccommerce.inventoryservice.app.web.entity.InventoryRegister;
import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.service.InventoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.maccommerce.inventoryservice.app.web.entity.mapper.InventoryMapper.toDomainEntity;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping("/inventories")
public class InventoryController {

    private final InventoryService service;

    @Autowired public InventoryController(InventoryService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<Inventory> findAll() {
        log.info("Incoming request to get all inventories.");
        return service.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Inventory findById(@PathVariable String id) {
        log.info("Incoming request to get inventory with id = {}.", id);
        return service.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Inventory save(@RequestBody InventoryRegister register) {
        log.info("Incoming request to save a new inventory.");
        return service.save(toDomainEntity(register));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Inventory update(@PathVariable String id, @RequestBody InventoryRegister register) {
        log.info("Incoming request to update inventory with id = {}.", id);
        return service.update(id, toDomainEntity(register));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable String id) {
        log.info("Incoming request to delete inventory with id = {}.", id);
        service.delete(id);
    }

}
