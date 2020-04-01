package br.com.maccommerce.storeservice.domain.service.impl

import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.exception.NotFoundException
import br.com.maccommerce.storeservice.domain.repository.StoreRepository
import br.com.maccommerce.storeservice.domain.service.StoreService

class StoreServiceImpl(private val repository: StoreRepository) : StoreService {

    override fun save(store: Store) = repository.persist(store)

    override fun update(id: String, store: Store) =
        repository.findById(id)?.let { repository.update(id, store) }
            ?: throw NotFoundException("Store with id $id was not found.")

    override fun delete(id: String) = repository.delete(id)

    override fun findAll() = repository.findAll()

    override fun findById(id: String) =
        repository.findById(id) ?: throw NotFoundException("Store with id $id was not found.")

}
