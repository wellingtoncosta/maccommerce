package br.com.maccommerce.storeservice.app.config

import br.com.maccommerce.storeservice.app.web.controller.StoreController
import br.com.maccommerce.storeservice.domain.repository.StoreRepository
import br.com.maccommerce.storeservice.domain.service.StoreService
import br.com.maccommerce.storeservice.domain.service.impl.StoreServiceImpl
import br.com.maccommerce.storeservice.resources.repository.StoreRepositoryImpl
import org.koin.dsl.module

private val repositoryModule = module {
    single<StoreRepository> { StoreRepositoryImpl() }
}

private val serviceModule = module {
    single<StoreService> { StoreServiceImpl(get()) }
}

private val controllerModule = module {
    single { StoreController(get()) }
}

val appModules = repositoryModule + serviceModule + controllerModule
