package br.com.maccommerce.inventoryservice.mock;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;

public class DatabaseMock {

    private final EmbeddedPostgres embeddedPostgres;

    public DatabaseMock() throws IOException {
        this.embeddedPostgres = EmbeddedPostgres.builder().setPort(5433).start();
    }

    public void stopServer() throws IOException {
        embeddedPostgres.close();
    }

}
