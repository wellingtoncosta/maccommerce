package br.com.maccommerce.productservice.domain.service

interface CrudService<T> {

    fun save(entity: T): T

    fun update(id: String, entity: T): T

    fun delete(id: String)

    fun findAll(): List<T>

    fun findById(id: String): T

}
