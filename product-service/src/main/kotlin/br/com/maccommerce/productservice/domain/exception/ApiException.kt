package br.com.maccommerce.productservice.domain.exception

enum class ApiExceptionType {

    BAD_REQUEST,
    DATABASE_ERROR,
    INTERNAL_ERROR,
    NOT_FOUND

}

open class ApiException(
    val type: ApiExceptionType,
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message)
