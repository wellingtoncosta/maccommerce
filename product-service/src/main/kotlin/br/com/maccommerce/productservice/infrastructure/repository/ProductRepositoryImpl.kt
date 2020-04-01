package br.com.maccommerce.productservice.infrastructure.repository

import br.com.maccommerce.productservice.commons.Loggable
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.exception.DatabaseException
import br.com.maccommerce.productservice.domain.repository.ProductRepository
import br.com.maccommerce.productservice.infrastructure.entity.CategoryTable
import br.com.maccommerce.productservice.infrastructure.entity.ProductTable
import br.com.maccommerce.productservice.infrastructure.extension.toCategory
import br.com.maccommerce.productservice.infrastructure.extension.toProduct
import io.azam.ulidj.ULID
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class ProductRepositoryImpl : ProductRepository {

    override fun persist(entity: Product) = transactionCatching {
        ULID.random().let { ulid ->
            ProductTable.insert {
                it[id] = ulid
                it[name] = entity.name
                it[description] = entity.description
                it[price] = entity.price
                it[categoryId] = entity.category.id!!
            }.let { entity.copy(
                id = ulid,
                category = CategoryTable.select { CategoryTable.id eq entity.category.id!! }.first().toCategory())
            }
        }
    }.also { logger.info("Product persisted successfully with id = ${it.id}") }

    override fun update(id: String, entity: Product) = transactionCatching {
        ProductTable.update({ ProductTable.id eq id }) {
            it[name] = entity.name
            it[description] = entity.description
            it[price] = entity.price
        }.let {
            if(it == 0) throw DatabaseException(message = "Product with id = $id was not updated.").also { e ->
                logger.error(e.message)
            }
            else entity.copy(id = id)
        }
    }.also { logger.info("Product with id = ${it.id} was updated successfully") }

    override fun delete(id: String) = transactionCatching {
        ProductTable.deleteWhere { ProductTable.id eq id }
    }.let { Unit }.also { logger.info("Product with id = $id deleted successfully") }

    override fun findAll() = transactionCatching {
        ProductTable.innerJoin(CategoryTable)
            .selectAll().map { it.toProduct() }
    }.also { logger.info("All products fetched successfully.") }

    override fun findById(id: String) = transactionCatching {
        ProductTable.innerJoin(CategoryTable)
            .select { ProductTable.id eq id }.firstOrNull()?.toProduct()
    }.also { logger.info("Product with id = $id was ${if(it == null) "not" else ""} fetched successfully") }

    companion object : Loggable()

}
