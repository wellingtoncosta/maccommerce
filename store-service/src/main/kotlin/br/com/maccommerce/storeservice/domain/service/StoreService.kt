package br.com.maccommerce.storeservice.domain.service

import br.com.maccommerce.storeservice.domain.entity.Store

interface StoreService {

    fun save(store: Store): Store

    fun update(id: String, store: Store): Store

    fun delete(id: String)

    fun findAll(): List<Store>

    fun findById(id: String): Store

}
