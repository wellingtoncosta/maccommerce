package br.com.maccommerce.productservice.domain.exception

import br.com.maccommerce.productservice.domain.exception.ApiExceptionType.BAD_REQUEST

class BadRequestException(override val message: String) : ApiException(BAD_REQUEST, message)