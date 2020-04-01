package br.com.maccommerce.productservice.domain.entity

data class Product(
    val id: String? = null,
    val name: String,
    val description: String = "",
    val price: Double,
    val category: Category
)
