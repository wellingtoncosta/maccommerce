package br.com.maccommerce.storeservice.domain.exception

import br.com.maccommerce.storeservice.domain.exception.ApiExceptionType.BAD_REQUEST

class BadRequestException(override val message: String) : ApiException(BAD_REQUEST, message)