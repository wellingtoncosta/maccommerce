package br.com.maccommerce.inventoryservice.domain.service;

import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.entity.Product;
import br.com.maccommerce.inventoryservice.domain.exception.InventoryNotFoundException;
import br.com.maccommerce.inventoryservice.domain.exception.ProductNotFoundException;
import br.com.maccommerce.inventoryservice.domain.repository.InventoryRepository;
import br.com.maccommerce.inventoryservice.domain.repository.ProductRepository;
import br.com.maccommerce.inventoryservice.domain.service.impl.InventoryServiceImpl;
import br.com.maccommerce.inventoryservice.mock.InventoryMock;
import br.com.maccommerce.inventoryservice.mock.ProductMock;
import io.azam.ulidj.ULID;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.nCopies;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("Runs all tests for service layer of inventory entity")
class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private ProductRepository productRepository;
    private InventoryService service;

    @BeforeEach void beforeEach() {
        inventoryRepository = Mockito.mock(InventoryRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        service = new InventoryServiceImpl(inventoryRepository, productRepository);
    }

    @Test
    @DisplayName("should find all inventories with empty result")
    void findAllWithEmptyResult() {
        Mockito.when(inventoryRepository.findAll()).thenReturn(Lists.emptyList());

        List<Inventory> inventories = service.findAll();

        assertTrue(inventories.isEmpty());

        Mockito.verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("should find all inventories with one result")
    void findAllWithOneResult() {
        Inventory inventory = InventoryMock.getOne();
        Product product = ProductMock.getOne();
        product.setId(inventory.getProduct().getId());

        Mockito.when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        Mockito.when(inventoryRepository.findAll())
                .thenReturn(Lists.list(inventory));

        List<Inventory> inventories = service.findAll();

        assertFalse(inventories.isEmpty());

        inventories.forEach(it -> {
            assertEquals(inventory.getId(), it.getId());
            assertEquals(inventory.getDescription(), it.getDescription());
            assertEquals(inventory.getAmount(), it.getAmount());
            assertEquals(product, it.getProduct());
        });

        Mockito.verify(productRepository).findById(product.getId());
        Mockito.verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("should find all inventories with one result and not find product")
    void findAllWithOneResultAndNotFindProduct() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(productRepository.findById(inventory.getProduct().getId()))
                .thenReturn(Optional.empty());

        Mockito.when(inventoryRepository.findAll())
                .thenReturn(Lists.list(inventory));

        List<Inventory> inventories = service.findAll();

        assertFalse(inventories.isEmpty());

        inventories.forEach(it -> {
            assertEquals(inventory.getId(), it.getId());
            assertEquals(inventory.getDescription(), it.getDescription());
            assertEquals(inventory.getAmount(), it.getAmount());
            assertEquals(inventory.getProduct().getId(), it.getProduct().getId());
            assertNull(it.getProduct().getName());
            assertNull(it.getProduct().getDescription());
            assertNull(it.getProduct().getPrice());
            assertNull(it.getProduct().getCategory());
        });

        Mockito.verify(productRepository).findById(inventory.getProduct().getId());
        Mockito.verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("should find all inventories with three results")
    void findAllWithThreeResults() {
        List<Inventory> inventories = nCopies(3, InventoryMock.getOne());

        String productId = ULID.random();
        inventories.forEach(inventory -> inventory.getProduct().setId(productId));

        Product product = ProductMock.getOne();
        product.setId(productId);

        Mockito.when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        Mockito.when(inventoryRepository.findAll())
                .thenReturn(inventories);

        List<Inventory> result = service.findAll();

        assertFalse(result.isEmpty());

        for(int i = 0; i < inventories.size(); i++) {
            assertEquals(inventories.get(i).getId(), result.get(i).getId());
            assertEquals(inventories.get(i).getDescription(), result.get(i).getDescription());
            assertEquals(inventories.get(i).getAmount(), result.get(i).getAmount());
            assertEquals(product, result.get(i).getProduct());
        }

        Mockito.verify(productRepository, Mockito.times(3)).findById(product.getId());
        Mockito.verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("should find all inventories with three results and not find product")
    void findAllWithThreeResultsAndNotFindProduct() {
        List<Inventory> inventories = nCopies(3, InventoryMock.getOne());

        String productId = ULID.random();
        inventories.forEach(inventory -> inventory.getProduct().setId(productId));

        Mockito.when(productRepository.findById(productId))
                .thenReturn(Optional.empty());

        Mockito.when(inventoryRepository.findAll())
                .thenReturn(inventories);

        List<Inventory> result = service.findAll();

        assertFalse(result.isEmpty());

        for(int i = 0; i < inventories.size(); i++) {
            assertEquals(inventories.get(i).getId(), result.get(i).getId());
            assertEquals(inventories.get(i).getDescription(), result.get(i).getDescription());
            assertEquals(inventories.get(i).getAmount(), result.get(i).getAmount());
            assertEquals(inventories.get(i).getProduct().getId(), result.get(i).getProduct().getId());
            assertNull(result.get(i).getProduct().getName());
            assertNull(result.get(i).getProduct().getDescription());
            assertNull(result.get(i).getProduct().getPrice());
            assertNull(result.get(i).getProduct().getCategory());
        }

        Mockito.verify(productRepository, Mockito.times(3)).findById(productId);
        Mockito.verify(inventoryRepository).findAll();
    }

    @Test
    @DisplayName("should find an inventory by id successfully")
    void findByIdSuccessfully() {
        Inventory inventory = InventoryMock.getOne();
        Product product = ProductMock.getOne();
        product.setId(inventory.getProduct().getId());

        Mockito.when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        Mockito.when(inventoryRepository.findById(inventory.getId()))
                .thenReturn(Optional.of(inventory));

        Inventory result = service.findById(inventory.getId());

        assertNotNull(result);
        assertEquals(inventory, result);

        Mockito.verify(productRepository).findById(product.getId());
        Mockito.verify(inventoryRepository).findById(inventory.getId());
    }

    @Test
    @DisplayName("should find an inventory by id successfully and not find product")
    void findByIdSuccessfullyAndNotFindProduct() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(productRepository.findById(inventory.getProduct().getId()))
                .thenReturn(Optional.empty());

        Mockito.when(inventoryRepository.findById(inventory.getId()))
                .thenReturn(Optional.of(inventory));

        Inventory result = service.findById(inventory.getId());

        assertNotNull(result);
        assertEquals(inventory, result);

        Mockito.verify(productRepository).findById(inventory.getProduct().getId());
        Mockito.verify(inventoryRepository).findById(inventory.getId());
    }

    @Test
    @DisplayName("should not find an inventory by id")
    void notFindById() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> service.findById(inventory.getId()));

        Mockito.verify(productRepository, Mockito.times(0)).findById(any());
        Mockito.verify(inventoryRepository).findById(inventory.getId());
    }

    @Test
    @DisplayName("should save a new inventory")
    void saveNewInventorySuccessfully() {
        Inventory inventory = InventoryMock.getOne();
        Product product = ProductMock.getOne();
        product.setId(inventory.getProduct().getId());

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(inventoryRepository.save(inventory)).thenReturn(inventory);

        Inventory result = service.save(inventory);

        assertEquals(inventory, result);

        Mockito.verify(productRepository).findById(product.getId());
        Mockito.verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("should not save a new inventory when product is not found")
    void notSaveNewInventoryWhenProductIsNotFound() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(productRepository.findById(inventory.getProduct().getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->  service.save(inventory));

        Mockito.verify(productRepository).findById(inventory.getProduct().getId());
        Mockito.verify(inventoryRepository, Mockito.times(0)).save(inventory);
    }

    @Test
    @DisplayName("should update an inventory sucessfully")
    void updateInventorySucessfully() {
        Inventory inventory = InventoryMock.getOne();
        Product product = ProductMock.getOne();
        product.setId(inventory.getProduct().getId());

        Mockito.when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(inventoryRepository.save(inventory)).thenReturn(inventory);

        Inventory result = service.update(inventory.getId(), inventory);

        assertEquals(inventory, result);

        Mockito.verify(inventoryRepository).findById(inventory.getId());
        Mockito.verify(productRepository).findById(product.getId());
        Mockito.verify(inventoryRepository).save(inventory);
    }

    @Test
    @DisplayName("should not update an inventory when it does not exist")
    void notUpdateInventoryWhenItDoesNotExist() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> service.update(inventory.getId(), inventory));

        Mockito.verify(inventoryRepository).findById(inventory.getId());
        Mockito.verify(productRepository, Mockito.times(0)).findById(inventory.getProduct().getId());
        Mockito.verify(inventoryRepository, Mockito.times(0)).save(inventory);
    }

    @Test
    @DisplayName("should not update an inventory when product is not found")
    void notUpdateInventoryWhenProductIsNotFound() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.when(inventoryRepository.findById(inventory.getId()))
                .thenReturn(Optional.of(inventory));

        Mockito.when(productRepository.findById(inventory.getProduct().getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.update(inventory.getId(), inventory));

        Mockito.verify(inventoryRepository).findById(inventory.getId());
        Mockito.verify(productRepository).findById(inventory.getProduct().getId());
        Mockito.verify(inventoryRepository, Mockito.times(0)).save(inventory);
    }

    @Test
    @DisplayName("shoud delete an inventory successfully")
    void shouldDeleteInventorySuccessfully() {
        Inventory inventory = InventoryMock.getOne();

        Mockito.doNothing().when(inventoryRepository).delete(inventory.getId());

        service.delete(inventory.getId());

        Mockito.verify(inventoryRepository).delete(inventory.getId());
    }

}
