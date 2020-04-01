package br.com.maccommerce.productservice.domain.service.impl

import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.repository.ProductRepository
import br.com.maccommerce.productservice.domain.service.ProductService

class ProductServiceImpl(
    repository: ProductRepository
) : ProductService, CrudServiceImpl<Product>(repository)
