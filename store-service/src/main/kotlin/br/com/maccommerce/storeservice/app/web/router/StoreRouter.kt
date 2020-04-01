package br.com.maccommerce.storeservice.app.web.router

import br.com.maccommerce.storeservice.app.extension.delete
import br.com.maccommerce.storeservice.app.extension.get
import br.com.maccommerce.storeservice.app.extension.post
import br.com.maccommerce.storeservice.app.extension.put
import br.com.maccommerce.storeservice.app.web.controller.StoreController
import org.http4k.routing.RoutingHttpHandler
import org.koin.core.KoinComponent
import org.koin.core.inject

object StoreRouter : KoinComponent {

    private val controller by inject<StoreController>()

    operator fun invoke(): List<RoutingHttpHandler> = listOf(
        "/" post { controller.save(it) },

        "/{id}" put  { controller.update(it) },

        "/{id}" delete   { controller.delete(it) },

        "/" get { controller.findAll() },

        "/{id}" get { controller.findById(it) }
    ).map { it.withBasePath("/stores") }

}
