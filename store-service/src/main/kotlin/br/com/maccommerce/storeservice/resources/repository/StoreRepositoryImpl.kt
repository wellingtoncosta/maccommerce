package br.com.maccommerce.storeservice.resources.repository

import br.com.maccommerce.storeservice.commons.Loggable
import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.exception.DatabaseException
import br.com.maccommerce.storeservice.domain.repository.StoreRepository
import br.com.maccommerce.storeservice.resources.entity.StoreTable
import br.com.maccommerce.storeservice.resources.extension.toStore
import io.azam.ulidj.ULID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class StoreRepositoryImpl : StoreRepository {

    override fun persist(store: Store) = transactionCatching {
        ULID.random().let { ulid ->
            StoreTable.insert {
                it[id] = ulid
                it[name] = store.name
                it[description] = store.description
                it[address] = store.address
                it[number] = store.number
                it[postalCode] = store.postalCode
            }.let { store.copy(id = ulid) }
        }
    }.also { logger.info("Store persisted successfully with id = ${it.id}") }

    override fun update(id: String, store: Store) = transactionCatching {
        StoreTable.update({ StoreTable.id eq id }) {
            it[name] = store.name
            it[description] = store.description
        }.let {
            if(it == 0) throw DatabaseException(message = "Store with id = $id was not updated.").also { e ->
                logger.error(e.message)
            }
            else store.copy(id = id)
        }
    }.also { logger.info("Store with id = ${it.id} was updated successfully") }

    override fun delete(id: String) = transactionCatching {
        StoreTable.deleteWhere { StoreTable.id eq id }
    }.let { Unit }.also { logger.info("Store with id = $id deleted successfully") }

    override fun findAll() = transactionCatching {
        StoreTable.selectAll().map { it.toStore() }
    }.also { logger.info("All stores fetched successfully.") }

    override fun findById(id: String) = transactionCatching {
        StoreTable.select { StoreTable.id eq id }.firstOrNull()?.toStore()
    }.also { logger.info("Store with id = $id was ${if(it == null) "not" else ""} fetched successfully") }

    companion object : Loggable()

    private fun <T> transactionCatching(block: () -> T) = transaction {
        try { block() } catch (t: Throwable) { throw DatabaseException(cause = t) }
    }

}
