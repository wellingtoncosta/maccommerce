package br.com.maccommerce.productservice.domain.service.impl

import br.com.maccommerce.productservice.domain.exception.NotFoundException
import br.com.maccommerce.productservice.domain.repository.CrudRepository
import br.com.maccommerce.productservice.domain.service.CrudService

open class CrudServiceImpl<T> constructor(
    private val repository: CrudRepository<T>
) : CrudService<T> {

    override fun save(entity: T) = repository.persist(entity)

    override fun update(id: String, entity: T) =
        repository.findById(id)?.let { repository.update(id, entity) }
            ?: throw NotFoundException("Entity with id $id was not found.")

    override fun delete(id: String) = repository.delete(id)

    override fun findAll() = repository.findAll()

    override fun findById(id: String) =
        repository.findById(id) ?: throw NotFoundException("Entity with id $id was not found.")

}
