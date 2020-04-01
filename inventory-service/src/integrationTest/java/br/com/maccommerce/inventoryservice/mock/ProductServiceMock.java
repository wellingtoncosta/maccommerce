package br.com.maccommerce.inventoryservice.mock;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Objects;

public class ProductServiceMock {

    private final ObjectMapper mapper;
    private final MockWebServer server;

    public ProductServiceMock() throws IOException {
        this.mapper = new ObjectMapper();
        this.server = new MockWebServer();
        this.server.start(7000);
    }

    public void stopServer() throws IOException {
        server.shutdown();
    }

    public void dispatchFindById(Product product) {
        Dispatcher dispatcher = new Dispatcher() {
            @NotNull @Override public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                String endpoint = "/products/" + product.getId();
                try {
                    if (endpoint.equals(Objects.requireNonNull(recordedRequest.getPath()))) {
                        return new MockResponse()
                                .setResponseCode(200)
                                .setBody(mapper.writeValueAsString(product));
                    }
                    return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
                } catch (JsonProcessingException e) {
                    return new MockResponse().setResponseCode(404);
                }
            }
        };

        server.setDispatcher(dispatcher);
    }

    public void dispatchServerError(Product product) {
        Dispatcher dispatcher = new Dispatcher() {
            @NotNull @Override public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                String endpoint = "/products/" + product.getId();
                if (endpoint.equals(Objects.requireNonNull(recordedRequest.getPath()))) {
                    return new MockResponse().setResponseCode(500);
                }
                return new MockResponse().setResponseCode(HttpStatus.NOT_FOUND.value());
            }
        };

        server.setDispatcher(dispatcher);
    }

}
