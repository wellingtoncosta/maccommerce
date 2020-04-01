package br.com.maccommerce.inventoryservice.domain.repository;

import br.com.maccommerce.inventoryservice.domain.entity.Product;

import java.util.Optional;

public interface ProductRepository {

    Optional<Product> findById(String id);

}
