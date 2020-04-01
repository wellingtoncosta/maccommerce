package br.com.maccommerce.storeservice.resources.entity

import br.com.maccommerce.storeservice.resources.utils.VARCHAR_MAX
import io.azam.ulidj.ULID.ULID_LENGTH
import org.jetbrains.exposed.sql.Table

object StoreTable : Table("store") {

    val id = varchar("id", ULID_LENGTH).primaryKey()
    val name = varchar("name", VARCHAR_MAX)
    val description = varchar("description", VARCHAR_MAX).nullable()
    val address = varchar("address", VARCHAR_MAX)
    val number = varchar("number", VARCHAR_MAX)
    val postalCode = varchar("postal_code", VARCHAR_MAX)

}
