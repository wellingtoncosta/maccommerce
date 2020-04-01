package br.com.maccommerce.inventoryservice.resources.product.repository;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import br.com.maccommerce.inventoryservice.domain.repository.ProductRepository;
import br.com.maccommerce.inventoryservice.resources.product.retrofit.ProductApi;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import retrofit2.Response;

import java.util.Optional;

@Log4j2 @Repository public class ProductRepositoryImpl implements ProductRepository {

    private final ProductApi api;

    @Autowired public ProductRepositoryImpl(ProductApi api) {
        this.api = api;
    }

    @Override public Optional<Product> findById(String id) {
        try {
            Response<Product> response = api.findById(id).execute();
            int statusCode = response.code();
            if(statusCode == 200) {
                log.info("Product with id = {} was found.", id);
                return Optional.ofNullable(response.body());
            } else if(statusCode == 404) {
                log.info("Product with id = {} was not found.", id);
                return Optional.empty();
            } else {
                log.error("Could not get product for id = {}.", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Could not get product for id = {}.", id, e);
            return Optional.empty();
        }
    }

}
