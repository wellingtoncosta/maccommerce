package br.com.maccommerce.storeservice.domain.entity

data class Store(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val address: String,
    val number: String,
    val postalCode: String
)