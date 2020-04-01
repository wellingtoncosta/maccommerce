package br.com.maccommerce.productservice.infrastructure.repository

import br.com.maccommerce.productservice.domain.exception.DatabaseException
import org.jetbrains.exposed.sql.transactions.transaction

fun <T> transactionCatching(block: () -> T) = transaction {
    try { block() } catch (t: Throwable) { throw DatabaseException(cause = t) }
}