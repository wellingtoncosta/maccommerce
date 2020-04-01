package br.com.maccommerce.productservice.infrastructure.entity

import br.com.maccommerce.productservice.infrastructure.utils.VARCHAR_MAX
import io.azam.ulidj.ULID.ULID_LENGTH
import org.jetbrains.exposed.sql.Table

object ProductTable : Table("product") {

    val id = varchar("id", ULID_LENGTH).primaryKey()
    val name = varchar("name", VARCHAR_MAX)
    val description = varchar("description", VARCHAR_MAX).nullable()
    val price = double("price")
    val categoryId = varchar("category_id", ULID_LENGTH).references(CategoryTable.id)

}
