package br.com.maccommerce.inventoryservice.resources.product;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import br.com.maccommerce.inventoryservice.domain.repository.ProductRepository;
import br.com.maccommerce.inventoryservice.mock.ProductMock;
import br.com.maccommerce.inventoryservice.mock.ProductServiceMock;
import br.com.maccommerce.inventoryservice.resources.product.repository.ProductRepositoryImpl;
import br.com.maccommerce.inventoryservice.resources.product.retrofit.ProductApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Runs all tests for repository layer of product entity")
class ProductRepositoryTest {

    private static ProductServiceMock productService;
    private static ProductRepository repository;

    @BeforeAll static void beforeAll() throws IOException {
        productService = new ProductServiceMock();

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(500, TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:7000")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        repository = new ProductRepositoryImpl(retrofit.create(ProductApi.class));
    }

    @AfterAll static void afterAll() throws IOException {
        productService.stopServer();
    }

    @Test
    @DisplayName("should find a product by id")
    void findByIdSuccessfully() {
        Product product = ProductMock.getOne();

        productService.dispatchFindById(product);

        Optional<Product> result = repository.findById(product.getId());

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    @DisplayName("should not find a product by id when it does not exist")
    void findByIdWithFailureWhenItDoesNotExist() {
        Product product = ProductMock.getOne();

        productService.dispatchFindById(ProductMock.getOne());

        Optional<Product> result = repository.findById(product.getId());

        assertFalse(result.isPresent());

        productService.dispatchServerError(product);

        result = repository.findById(product.getId());

        assertFalse(result.isPresent());
    }

}
