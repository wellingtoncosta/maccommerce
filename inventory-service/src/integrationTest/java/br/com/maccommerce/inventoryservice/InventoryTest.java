package br.com.maccommerce.inventoryservice;

import br.com.maccommerce.inventoryservice.app.InventoryServiceApp;
import br.com.maccommerce.inventoryservice.app.web.entity.InventoryRegister;
import br.com.maccommerce.inventoryservice.config.RetrofitTestConfig;
import br.com.maccommerce.inventoryservice.domain.entity.Inventory;
import br.com.maccommerce.inventoryservice.domain.entity.Product;
import br.com.maccommerce.inventoryservice.domain.exception.ApiException;
import br.com.maccommerce.inventoryservice.domain.repository.InventoryRepository;
import br.com.maccommerce.inventoryservice.mock.DatabaseMock;
import br.com.maccommerce.inventoryservice.mock.InventoryMock;
import br.com.maccommerce.inventoryservice.mock.ProductMock;
import br.com.maccommerce.inventoryservice.mock.ProductServiceMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.flywaydb.test.annotation.FlywayTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

import static java.util.Collections.nCopies;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FlywayTest
@AutoConfigureMockMvc
@SpringBootTest(classes = { InventoryServiceApp.class })
@ContextConfiguration(classes = { RetrofitTestConfig.class })
@DisplayName("Runs all tests for inventory management feature")
public class InventoryTest {

    @Autowired protected Flyway flyway;
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper mapper;
    @Autowired protected InventoryRepository inventoryRepository;

    private static ProductServiceMock productService;
    private static DatabaseMock databaseMock;

    @BeforeAll static void init() throws IOException {
        productService = new ProductServiceMock();
        databaseMock = new DatabaseMock();
    }

    @BeforeEach void beforeEach() {
        flyway.clean();
        flyway.migrate();
    }

    @AfterAll static void tearDown() throws IOException {
        productService.stopServer();
        databaseMock.stopServer();
    }

    @Nested
    @DisplayName("GET /inventories")
    class FindAll {

        @Test
        @DisplayName("should get all inventories with empty result")
        void findAllWithEmptyResult() throws Exception {
            mockMvc.perform(get("/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("should get all inventories with one result")
        void findAllWithOneResult() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            inventoryRepository.save(inventory);

            mockMvc.perform(get("/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(inventory.getId())))
                    .andExpect(jsonPath("$[0].description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$[0].amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$[0].product.id", is(product.getId())))
                    .andExpect(jsonPath("$[0].product.name", is(product.getName())))
                    .andExpect(jsonPath("$[0].product.description", is(product.getDescription())))
                    .andExpect(jsonPath("$[0].product.price", is(product.getPrice())));
        }

        @Test
        @DisplayName("should get all inventories with one result when product is not obtained")
        void findAllWithOneResultWhenProductIsNotObtained() throws Exception {
            Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

            productService.dispatchFindById(ProductMock.getOne());

            inventoryRepository.save(inventory);

            mockMvc.perform(get("/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(inventory.getId())))
                    .andExpect(jsonPath("$[0].description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$[0].amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$[0].product.id", is(notNullValue())))
                    .andExpect(jsonPath("$[0].product.name", nullValue()))
                    .andExpect(jsonPath("$[0].product.description", nullValue()))
                    .andExpect(jsonPath("$[0].product.price", nullValue()));
        }

        @Test
        @DisplayName("should get all inventories with three results")
        void findAllWithThreeResults() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory1 = InventoryMock.getOne(product);
            Inventory inventory2 = InventoryMock.getOne(product);
            Inventory inventory3 = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            inventoryRepository.save(inventory1);
            inventoryRepository.save(inventory2);
            inventoryRepository.save(inventory3);

            mockMvc.perform(get("/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[:3].id", hasItems(inventory1.getId(), inventory2.getId(), inventory3.getId())))
                    .andExpect(jsonPath("$[:3].description", is(nCopies(3, inventory1.getDescription()))))
                    .andExpect(jsonPath("$[:3].amount", is(nCopies(3, inventory1.getAmount().intValue()))))
                    .andExpect(jsonPath("$[:3].product.id", is(nCopies(3, product.getId()))))
                    .andExpect(jsonPath("$[:3].product.name", is(nCopies(3, product.getName()))))
                    .andExpect(jsonPath("$[:3].product.description", is(nCopies(3, product.getDescription()))))
                    .andExpect(jsonPath("$[:3].product.price", is(nCopies(3, product.getPrice()))));
        }

        @Test
        @DisplayName("should get all inventories with three results  when product is not obtained")
        void findAllWithThreeResultsWhenProductIsNotObtained() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory1 = InventoryMock.getOne(product);
            Inventory inventory2 = InventoryMock.getOne(product);
            Inventory inventory3 = InventoryMock.getOne(product);

            productService.dispatchFindById(ProductMock.getOne());

            inventoryRepository.save(inventory1);
            inventoryRepository.save(inventory2);
            inventoryRepository.save(inventory3);

            mockMvc.perform(get("/inventories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[:3].id", hasItems(inventory1.getId(), inventory2.getId(), inventory3.getId())))
                    .andExpect(jsonPath("$[:3].description", is(nCopies(3, inventory1.getDescription()))))
                    .andExpect(jsonPath("$[:3].amount", is(nCopies(3, inventory1.getAmount().intValue()))))
                    .andExpect(jsonPath("$[:3].product.id", is(nCopies(3, product.getId()))))
                    .andExpect(jsonPath("$[:3].product.name", is(nCopies(3, null))))
                    .andExpect(jsonPath("$[:3].product.description", is(nCopies(3, null))))
                    .andExpect(jsonPath("$[:3].product.price", is(nCopies(3, null))));
        }

    }

    @Nested
    @DisplayName("GET /inventories/{id}")
    class FindById {

        @Test
        @DisplayName("should get a inventory by id")
        void findByIdWithResult() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            inventoryRepository.save(inventory);

            mockMvc.perform(get("/inventories/" + inventory.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(inventory.getId())))
                    .andExpect(jsonPath("$.description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$.amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$.product.id", is(product.getId())))
                    .andExpect(jsonPath("$.product.name", is(product.getName())))
                    .andExpect(jsonPath("$.product.description", is(product.getDescription())))
                    .andExpect(jsonPath("$.product.price", is(product.getPrice())));
        }

        @Test
        @DisplayName("should get a inventory by id with product not obtained")
        void findByIdWithResultAndProductNotObtained() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(ProductMock.getOne());

            inventoryRepository.save(inventory);

            mockMvc.perform(get("/inventories/" + inventory.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(inventory.getId())))
                    .andExpect(jsonPath("$.description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$.amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$.product.id", is(product.getId())))
                    .andExpect(jsonPath("$.product.name", nullValue()))
                    .andExpect(jsonPath("$.product.description", nullValue()))
                    .andExpect(jsonPath("$.product.price", nullValue()));
        }

        @Test
        @DisplayName("should not get a inventory by id when it does not exist")
        void findByIdWithNoResult() throws Exception {
            Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

            String message = String.format(
                    "Inventory with id = %s was not found.",
                    inventory.getId()
            );

            mockMvc.perform(get("/inventories/" + inventory.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type", is(ApiException.ApiExceptionType.NOT_FOUND.toString())))
                    .andExpect(jsonPath("$.message", is(message)));
        }

    }

    @Nested
    @DisplayName("POST /inventories")
    class Save {

        @Test
        @DisplayName("should save a new inventory")
        void saveSuccessfully() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            MockHttpServletRequestBuilder request = post("/inventories")
                    .content(mapper.writeValueAsString(new InventoryRegister(inventory)))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$.amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$.product.id", is(product.getId())))
                    .andExpect(jsonPath("$.product.name", is(product.getName())))
                    .andExpect(jsonPath("$.product.description", is(product.getDescription())))
                    .andExpect(jsonPath("$.product.price", is(product.getPrice())));
        }

        @Test
        @DisplayName("should not save a new inventory when product is not found")
        void saveWithFailureWhenProductIsNotFound() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(ProductMock.getOne());

            MockHttpServletRequestBuilder request = post("/inventories")
                    .content(mapper.writeValueAsString(new InventoryRegister(inventory)))
                    .contentType(MediaType.APPLICATION_JSON);

            String message = String.format(
                    "Product with id = %s was not found.",
                    product.getId()
            );

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type", is(ApiException.ApiExceptionType.NOT_FOUND.toString())))
                    .andExpect(jsonPath("$.message", is(message)));
        }

    }

    @Nested
    @DisplayName("PUT /inventories/{id}")
    class Update {

        @Test
        @DisplayName("should update a inventory")
        void updateSuccessfully() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            inventoryRepository.save(inventory);

            inventory.setAmount(200L);

            MockHttpServletRequestBuilder request = put("/inventories/" + inventory.getId())
                    .content(mapper.writeValueAsString(new InventoryRegister(inventory)))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", notNullValue()))
                    .andExpect(jsonPath("$.description", is(inventory.getDescription())))
                    .andExpect(jsonPath("$.amount", is(inventory.getAmount().intValue())))
                    .andExpect(jsonPath("$.product.id", is(product.getId())))
                    .andExpect(jsonPath("$.product.name", is(product.getName())))
                    .andExpect(jsonPath("$.product.description", is(product.getDescription())))
                    .andExpect(jsonPath("$.product.price", is(product.getPrice())));
        }

        @Test
        @DisplayName("should not update a inventory when it does not exist in database")
        void updateWithFailureWhenItDoesNotExistInDatabase() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(product);

            inventoryRepository.save(InventoryMock.getOne(product));

            MockHttpServletRequestBuilder request = put("/inventories/" + inventory.getId())
                    .content(mapper.writeValueAsString(new InventoryRegister(inventory)))
                    .contentType(MediaType.APPLICATION_JSON);

            String message = String.format(
                    "Inventory with id = %s was not found.",
                    inventory.getId()
            );

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type", is(ApiException.ApiExceptionType.NOT_FOUND.toString())))
                    .andExpect(jsonPath("$.message", is(message)));
        }

        @Test
        @DisplayName("should not update a inventory when product is not found")
        void updateWithFailureWhenProductIsNotFound() throws Exception {
            Product product = ProductMock.getOne();
            Inventory inventory = InventoryMock.getOne(product);

            productService.dispatchFindById(ProductMock.getOne());

            inventoryRepository.save(inventory);

            MockHttpServletRequestBuilder request = put("/inventories/" + inventory.getId())
                    .content(mapper.writeValueAsString(new InventoryRegister(inventory)))
                    .contentType(MediaType.APPLICATION_JSON);

            String message = String.format(
                    "Product with id = %s was not found.",
                    product.getId()
            );

            mockMvc.perform(request)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.type", is(ApiException.ApiExceptionType.NOT_FOUND.toString())))
                    .andExpect(jsonPath("$.message", is(message)));
        }

    }

    @Nested
    @DisplayName("DELETE /inventories{id}")
    class Delete {

        @Test
        @DisplayName("should delete a inventory by id when it exists in database")
        void deleteByIdWhenItExistsInDatabase() throws Exception {
            Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

            inventoryRepository.save(inventory);

            mockMvc.perform(delete("/inventories/" + inventory.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("should delete a inventory by id when it does not exist in database")
        void deleteByIdWhenItDoesNotExistInDatabase() throws Exception {
            Inventory inventory = InventoryMock.getOne(ProductMock.getOne());

            mockMvc.perform(delete("/inventories/" + inventory.getId()))
                    .andExpect(status().isNoContent());
        }

    }

}
