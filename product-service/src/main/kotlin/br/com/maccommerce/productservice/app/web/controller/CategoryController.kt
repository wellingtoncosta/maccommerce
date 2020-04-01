package br.com.maccommerce.productservice.app.web.controller

import br.com.maccommerce.productservice.app.extension.getIdFromPath
import br.com.maccommerce.productservice.app.extension.jsonBody
import br.com.maccommerce.productservice.commons.Loggable
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.service.CategoryService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class CategoryController(private val service: CategoryService) : CrudController {

    override fun save(request: Request) = logger.info("Incoming request to save a new category").let {
        request.jsonBody<Category>().let { Response(Status.CREATED).jsonBody(service.save(it)) }
    }

    override fun update(request: Request) = logger.info("Incoming request to update a category").let {
        request.jsonBody<Category>().let { category ->
            request.getIdFromPath { Response(Status.OK).jsonBody(service.update(it, category)) }
        }
    }

    override fun delete(request: Request) = logger.info("Incoming request to delete a category").let {
        request.getIdFromPath { Response(Status.NO_CONTENT).jsonBody(service.delete(it)) }
    }

    override fun findAll(request: Request) = logger.info("Incoming request to get all categories").let {
        Response(Status.OK).jsonBody(service.findAll())
    }

    override fun findById(request: Request) = logger.info("Incoming request to get a category by id").let {
        request.getIdFromPath { Response(Status.OK).jsonBody(service.findById(it)) }
    }

    companion object : Loggable()

}
