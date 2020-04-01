package br.com.maccommerce.productservice.app.web.router

import br.com.maccommerce.productservice.app.web.controller.CrudController
import br.com.maccommerce.productservice.app.web.controller.ProductController
import org.koin.core.KoinComponent
import org.koin.core.get

object ProductRouter : KoinComponent, CrudRouter {

    override val controller: CrudController get() = get<ProductController>()

    override fun invoke() = super.invoke().map { it.withBasePath("/products") }

}
