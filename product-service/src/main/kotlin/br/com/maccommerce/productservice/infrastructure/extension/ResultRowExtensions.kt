package br.com.maccommerce.productservice.infrastructure.extension

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.infrastructure.entity.CategoryTable
import br.com.maccommerce.productservice.infrastructure.entity.ProductTable
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toCategory() = Category(
    id = this[CategoryTable.id],
    name = this[CategoryTable.name],
    description = this[CategoryTable.description] ?: ""
)

fun ResultRow.toProduct() = Product(
    id = this[ProductTable.id],
    name = this[ProductTable.name],
    description = this[ProductTable.description] ?: "",
    price = this[ProductTable.price],
    category = this.toCategory()
)
