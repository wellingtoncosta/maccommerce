package br.com.maccommerce.inventoryservice.resources.product;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import br.com.maccommerce.inventoryservice.mock.ProductMock;
import br.com.maccommerce.inventoryservice.mock.ProductServiceMock;
import br.com.maccommerce.inventoryservice.resources.product.retrofit.ProductApi;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Runs all tests for product service API communication")
class ProductApiTest {

    private static ProductServiceMock productService;
    private static ProductApi api;

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

        api = retrofit.create(ProductApi.class);
    }

    @AfterAll static void afterAll() throws IOException {
        productService.stopServer();
    }

    @Test
    @DisplayName("should find a product by id")
    void findByIdSuccessfully() throws IOException {
        Product product = ProductMock.getOne();

        productService.dispatchFindById(product);

        Response<Product> response = api.findById(product.getId()).execute();

        assertEquals(200, response.code());
        assertEquals(product, response.body());
    }

    @Test
    @DisplayName("should not find a product by id when it does not exist")
    void findByIdWithFailiureWhenItDoesNotExist() throws IOException {
        Product product = ProductMock.getOne();

        productService.dispatchFindById(ProductMock.getOne());

        Response<Product> response = api.findById(product.getId()).execute();

        assertEquals(404, response.code());
    }

    @Test
    @DisplayName("should not find a product by id when an internal server error has occurred")
    void findByIdWithFailiureWhenAnInternalServerErrorHasOccurred() throws IOException {
        Product product = ProductMock.getOne();

        productService.dispatchServerError(product);

        Response<Product> response = api.findById(product.getId()).execute();

        assertEquals(500, response.code());
    }

}
