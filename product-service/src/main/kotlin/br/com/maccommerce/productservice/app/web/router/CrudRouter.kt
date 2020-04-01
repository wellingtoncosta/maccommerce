package br.com.maccommerce.productservice.app.web.router

import br.com.maccommerce.productservice.app.extension.delete
import br.com.maccommerce.productservice.app.extension.get
import br.com.maccommerce.productservice.app.extension.post
import br.com.maccommerce.productservice.app.extension.put
import br.com.maccommerce.productservice.app.web.controller.CrudController
import org.http4k.routing.RoutingHttpHandler

interface CrudRouter {

    val controller: CrudController

    operator fun invoke(): Collection<RoutingHttpHandler> = mutableListOf(
        "/" post { controller.save(it) },

        "/{id}" put  { controller.update(it) },

        "/{id}" delete   { controller.delete(it) },

        "/" get { controller.findAll(it) },

        "/{id}" get { controller.findById(it) }
    )

}
