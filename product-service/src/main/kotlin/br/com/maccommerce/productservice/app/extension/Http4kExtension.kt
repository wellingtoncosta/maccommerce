package br.com.maccommerce.productservice.app.extension

import br.com.maccommerce.productservice.domain.exception.BadRequestException
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.bind
import org.http4k.routing.path

infix fun String.get(action: HttpHandler) = this bind GET to action

infix fun String.post(action: HttpHandler) = this bind POST to action

infix fun String.put(action: HttpHandler) = this bind PUT to action

infix fun String.delete(action: HttpHandler) = this bind DELETE to action

inline fun <reified T : Any> Request.jsonBody() = Jackson.asA(this.bodyString(), T::class)

fun Response.jsonBody(target: Any) = this.body(Jackson.asJsonString(target))

inline fun <reified T : Any> Response.bodyAsJson() = Jackson.asA(this.bodyString(), T::class)

fun <T> Request.getIdFromPath(body: (String) -> T) =
    this.path("id")?.let { body(it) } ?: throw BadRequestException("Parameter 'id' was not sent in path.")
