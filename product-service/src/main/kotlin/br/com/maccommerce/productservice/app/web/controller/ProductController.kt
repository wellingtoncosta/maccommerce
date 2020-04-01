package br.com.maccommerce.productservice.app.web.controller

import br.com.maccommerce.productservice.app.extension.getIdFromPath
import br.com.maccommerce.productservice.app.extension.jsonBody
import br.com.maccommerce.productservice.app.web.entity.ProductRequest
import br.com.maccommerce.productservice.app.web.entity.toProduct
import br.com.maccommerce.productservice.commons.Loggable
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.service.ProductService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class ProductController(private val service: ProductService) : CrudController {

    override fun save(request: Request) = logger.info("Incoming request to save a new product").let {
        request.jsonBody<ProductRequest>().let { Response(Status.CREATED).jsonBody(service.save(it.toProduct())) }
    }

    override fun update(request: Request) = logger.info("Incoming request to update a product").let {
        request.jsonBody<Product>().let { product ->
            request.getIdFromPath { Response(Status.OK).jsonBody(service.update(it, product)) }
        }
    }

    override fun delete(request: Request) = logger.info("Incoming request to delete a product").let {
        request.getIdFromPath { Response(Status.NO_CONTENT).jsonBody(service.delete(it)) }
    }

    override fun findAll(request: Request) = logger.info("Incoming request to get all products").let {
        Response(Status.OK).jsonBody(service.findAll())
    }

    override fun findById(request: Request) = logger.info("Incoming request to get a product by id").let {
        request.getIdFromPath { Response(Status.OK).jsonBody(service.findById(it)) }
    }

    companion object : Loggable()

}
