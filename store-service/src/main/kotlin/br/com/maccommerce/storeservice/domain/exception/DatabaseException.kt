package br.com.maccommerce.storeservice.domain.exception

import br.com.maccommerce.storeservice.domain.exception.ApiExceptionType.DATABASE_ERROR

class DatabaseException(
    override val message: String = "Could not perform the database operation.",
    override val cause: Throwable? = null
) : ApiException(DATABASE_ERROR, message)