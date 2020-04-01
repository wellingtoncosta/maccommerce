package br.com.maccommerce.productservice.app.web.entity

import br.com.maccommerce.productservice.domain.exception.ApiException
import br.com.maccommerce.productservice.domain.exception.ApiExceptionType

data class ApiExceptionResponse(val type: ApiExceptionType, val message: String = "Unknown exception.")

fun ApiException.toResponse() = ApiExceptionResponse(this.type, this.message)