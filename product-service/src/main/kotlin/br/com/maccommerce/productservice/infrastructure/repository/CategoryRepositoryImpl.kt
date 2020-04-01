package br.com.maccommerce.productservice.infrastructure.repository

import br.com.maccommerce.productservice.commons.Loggable
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.exception.DatabaseException
import br.com.maccommerce.productservice.domain.repository.CategoryRepository
import br.com.maccommerce.productservice.infrastructure.entity.CategoryTable
import br.com.maccommerce.productservice.infrastructure.extension.toCategory
import io.azam.ulidj.ULID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class CategoryRepositoryImpl : CategoryRepository {

    override fun persist(entity: Category) = transactionCatching {
        ULID.random().let { ulid ->
            CategoryTable.insert {
                it[id] = ulid
                it[name] = entity.name
                it[description] = entity.description
            }.let { entity.copy(id = ulid) }
        }
    }.also { logger.info("Category persisted successfully with id = ${it.id}") }

    override fun update(id: String, entity: Category) = transactionCatching {
        CategoryTable.update({ CategoryTable.id eq id }) {
            it[name] = entity.name
            it[description] = entity.description
        }.let {
            if(it == 0) throw DatabaseException(message = "Category with id = $id was not updated.").also { e ->
                logger.error(e.message)
            }
            else entity.copy(id = id)
        }
    }.also { logger.info("Category with id = ${it.id} was updated successfully") }

    override fun delete(id: String) = transactionCatching {
        CategoryTable.deleteWhere { CategoryTable.id eq id }
    }.let { Unit }.also { logger.info("Category with id = $id deleted successfully") }

    override fun findAll() = transactionCatching {
        CategoryTable.selectAll().map { it.toCategory() }
    }.also { logger.info("All categories fetched successfully.") }

    override fun findById(id: String) = transactionCatching {
        CategoryTable.select { CategoryTable.id eq id }.firstOrNull()?.toCategory()
    }.also { logger.info("Category with id = $id was ${if(it == null) "not" else ""} fetched successfully") }

    companion object : Loggable()

}
