package br.com.maccommerce.productservice.domain.exception

import br.com.maccommerce.productservice.domain.exception.ApiExceptionType.NOT_FOUND

class NotFoundException(override val message: String) : ApiException(NOT_FOUND, message)