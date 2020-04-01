package br.com.maccommerce.productservice.app.config

import br.com.maccommerce.productservice.app.web.controller.CategoryController
import br.com.maccommerce.productservice.app.web.controller.ProductController
import br.com.maccommerce.productservice.domain.repository.CategoryRepository
import br.com.maccommerce.productservice.domain.repository.ProductRepository
import br.com.maccommerce.productservice.domain.service.CategoryService
import br.com.maccommerce.productservice.domain.service.ProductService
import br.com.maccommerce.productservice.domain.service.impl.CategoryServiceImpl
import br.com.maccommerce.productservice.domain.service.impl.ProductServiceImpl
import br.com.maccommerce.productservice.infrastructure.repository.CategoryRepositoryImpl
import br.com.maccommerce.productservice.infrastructure.repository.ProductRepositoryImpl
import org.koin.dsl.module

private val repositoryModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<ProductRepository> { ProductRepositoryImpl() }
}

private val serviceModule = module {
    single<CategoryService> { CategoryServiceImpl(get()) }
    single<ProductService> { ProductServiceImpl(get()) }
}

private val controllerModule = module {
    single { CategoryController(get()) }
    single { ProductController(get()) }
}

val appModules = repositoryModule + serviceModule + controllerModule
