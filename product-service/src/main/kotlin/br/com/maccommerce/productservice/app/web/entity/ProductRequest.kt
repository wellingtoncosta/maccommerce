package br.com.maccommerce.productservice.app.web.entity

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product

data class ProductRequest(
    val id: String? = null,
    val name: String,
    val description: String = "",
    val price: Double,
    val categoryId: String
)

fun ProductRequest.toProduct() = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    price = this.price,
    category = Category(id = this.categoryId, name = "")
)
