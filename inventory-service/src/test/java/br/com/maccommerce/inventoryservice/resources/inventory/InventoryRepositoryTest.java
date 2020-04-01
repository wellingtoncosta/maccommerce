package br.com.maccommerce.inventoryservice.resources.inventory;

import br.com.maccommerce.inventoryservice.app.InventoryServiceApp;
import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.repository.InventoryRepository;
import br.com.maccommerce.inventoryservice.mock.DatabaseMock;
import br.com.maccommerce.inventoryservice.mock.InventoryMock;
import br.com.maccommerce.inventoryservice.mock.ProductMock;
import io.azam.ulidj.ULID;
import org.flywaydb.core.Flyway;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@FlywayTest
@Transactional
@SpringBootTest(classes = InventoryServiceApp.class)
@DisplayName("Runs all tests for repository layer of inventory entity")
class InventoryRepositoryTest {

    @Autowired private Flyway flyway;
    @Autowired private InventoryRepository repository;

    private static DatabaseMock databaseMock;

    @BeforeAll static void beforeAll() throws IOException {
        databaseMock = new DatabaseMock();
    }

    @BeforeEach void beforeEach() {
        flyway.clean();
        flyway.migrate();
    }

    @AfterAll static void afterAkk() throws IOException {
        databaseMock.stopServer();
    }

    @Test
    @DisplayName("should find all inventories with empty result")
    void findAllWithEmptyResult() {
        List<Inventory> inventories = repository.findAll();

        assertTrue(inventories.isEmpty());
    }

    @Test
    @DisplayName("should find all inventories with one result")
    void findAllWithOneResult() {
        repository.save(InventoryMock.getOne(ProductMock.getOne()));

        List<Inventory> inventories = repository.findAll();

        assertEquals(1, inventories.size());
    }

    @Test
    @DisplayName("should find all inventories with three results")
    void findAllWithThreeResults() {
        repository.save(InventoryMock.getOne(ProductMock.getOne()));
        repository.save(InventoryMock.getOne(ProductMock.getOne()));
        repository.save(InventoryMock.getOne(ProductMock.getOne()));

        List<Inventory> inventories = repository.findAll();

        assertEquals(3, inventories.size());
    }

    @Test
    @DisplayName("should find by id")
    void findByIdSuccessfully() {
        Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

        repository.save(inventory);

        Optional<Inventory> optional = repository.findById(inventory.getId());

        assertTrue(optional.isPresent());

        Inventory result = optional.get();

        assertEquals(inventory.getId(), result.getId());
        assertEquals(inventory.getProduct().getId(), result.getProduct().getId());
        assertEquals(inventory.getDescription(), result.getDescription());
        assertEquals(inventory.getAmount(), result.getAmount());
    }

    @Test
    @DisplayName("should not find by id when it does not exist")
    void findByIdWithFailureWhenItDoesNotExist() {
        Optional<Inventory> optional = repository.findById(ULID.random());

        assertFalse(optional.isPresent());
    }

    @Test
    @DisplayName("should save a new inventory with success")
    void saveNewInventorySuccessfully() {
        List<Inventory> inventories = repository.findAll();

        assertTrue(inventories.isEmpty());

        repository.save(InventoryMock.getOne(ProductMock.getOne()));

        inventories = repository.findAll();

        assertEquals(1, inventories.size());
    }

    @Test
    @DisplayName("should not save a new inventory when id is not present")
    void saveNewInventoryWithFailureWhenIdIsNotPresent() {
        List<Inventory> inventories = repository.findAll();

        assertTrue(inventories.isEmpty());

        Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

        inventory.setId(null);

        assertThrows(JpaSystemException.class, () -> repository.save(inventory));
    }

    @Test
    @DisplayName("should update a inventory successfully")
    void updateInventorySuccessfully() {
        Inventory saved = repository.save(InventoryMock.getOne(ProductMock.getOne()));

        saved.setAmount(500L);
        saved.setDescription("Updated");

        Inventory updated = repository.save(saved);

        assertEquals(saved, updated);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("should delete a inventory successfully")
    void deleteInventorySucessfully() {
        Inventory saved = repository.save(InventoryMock.getOne(ProductMock.getOne()));

        assertEquals(1, repository.findAll().size());

        repository.delete(saved.getId());

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("should delete a inventory when it does not exist")
    void deleteInventoryWhenItDoesNotExist() {
        repository.save(InventoryMock.getOne(ProductMock.getOne()));

        assertEquals(1, repository.findAll().size());

        repository.delete(ULID.random());

        assertEquals(1, repository.findAll().size());
    }

}
