package br.com.maccommerce.storeservice.app.web.entity

import br.com.maccommerce.storeservice.domain.exception.ApiException
import br.com.maccommerce.storeservice.domain.exception.ApiExceptionType

data class ApiExceptionResponse(val type: ApiExceptionType, val message: String = "Unknown exception.")

fun ApiException.toResponse() = ApiExceptionResponse(this.type, this.message)