package br.com.maccommerce.storeservice.domain.exception

import br.com.maccommerce.storeservice.domain.exception.ApiExceptionType.NOT_FOUND

class NotFoundException(override val message: String) : ApiException(NOT_FOUND, message)