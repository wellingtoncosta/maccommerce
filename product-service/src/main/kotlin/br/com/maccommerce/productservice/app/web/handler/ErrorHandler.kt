package br.com.maccommerce.productservice.app.web.handler

import br.com.maccommerce.productservice.app.web.entity.ApiExceptionResponse
import br.com.maccommerce.productservice.app.web.entity.toResponse
import br.com.maccommerce.productservice.commons.Loggable
import br.com.maccommerce.productservice.domain.exception.ApiException
import br.com.maccommerce.productservice.domain.exception.ApiExceptionType
import org.http4k.core.Filter
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson

object ErrorHandler : Loggable() {

    operator fun invoke() = Filter { next ->
        { request ->
            try { next(request) } catch (exception: Throwable) {
                when(exception) {
                    is ApiException -> when(exception.type) {
                        ApiExceptionType.BAD_REQUEST ->
                            Status.BAD_REQUEST to exception.toResponse()
                                .also { logException(exception) }

                        ApiExceptionType.DATABASE_ERROR ->
                            Status.INTERNAL_SERVER_ERROR to exception.toResponse()
                                .also { logException(exception) }

                        ApiExceptionType.INTERNAL_ERROR ->
                            Status.INTERNAL_SERVER_ERROR to exception.toResponse()
                                .also { logException(exception) }

                        ApiExceptionType.NOT_FOUND ->
                            Status.NOT_FOUND to exception.toResponse()
                                .also { logException(exception) }
                    }

                    else ->
                        Status.INTERNAL_SERVER_ERROR to ApiExceptionResponse(ApiExceptionType.INTERNAL_ERROR)
                            .also { logException(exception) }

                }.let { Response(it.first).body(Jackson.asJsonObject(it.second).toString()) }
            }
        }
    }

    private fun logException(exception: Throwable) {
        logger.error("Handling ${exception.javaClass.canonicalName} with message = ${exception.message}", exception)
    }

}
