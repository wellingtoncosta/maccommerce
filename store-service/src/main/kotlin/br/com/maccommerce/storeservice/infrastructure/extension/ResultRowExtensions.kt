package br.com.maccommerce.storeservice.infrastructure.extension

import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.infrastructure.entity.StoreTable
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toStore() = Store(
    id = this[StoreTable.id],
    name = this[StoreTable.name],
    description = this[StoreTable.description],
    address = this[StoreTable.address],
    number = this[StoreTable.number],
    postalCode = this[StoreTable.postalCode]
)
