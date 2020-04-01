package br.com.maccommerce.inventoryservice.mock;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import io.azam.ulidj.ULID;

public final class ProductMock {

    public static Product getOne() {
        Product.Category category = new Product.Category();
        category.setId(ULID.random());
        category.setName("Test");
        category.setDescription("Test");

        Product product = new Product();
        product.setId(ULID.random());
        product.setName("Test");
        product.setDescription("Test");
        product.setPrice(99.90);
        product.setCategory(category);

        return product;
    }

}
