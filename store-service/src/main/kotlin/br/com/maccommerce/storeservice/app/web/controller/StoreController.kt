package br.com.maccommerce.storeservice.app.web.controller

import br.com.maccommerce.storeservice.app.extension.getIdFromPath
import br.com.maccommerce.storeservice.app.extension.jsonBody
import br.com.maccommerce.storeservice.commons.Loggable
import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.service.StoreService
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class StoreController(private val service: StoreService) {

    fun save(request: Request) = logger.info("Incoming request to save a new store").let {
        request.jsonBody<Store>().let { Response(Status.CREATED).jsonBody(service.save(it)) }
    }

    fun update(request: Request) = logger.info("Incoming request to update a store").let {
        request.jsonBody<Store>().let { store ->
            request.getIdFromPath { Response(Status.OK).jsonBody(service.update(it, store)) }
        }
    }

    fun delete(request: Request) = logger.info("Incoming request to delete a store").let {
        request.getIdFromPath { Response(Status.NO_CONTENT).jsonBody(service.delete(it)) }
    }

    fun findAll() = logger.info("Incoming request to get all stores").let {
        Response(Status.OK).jsonBody(service.findAll())
    }

    fun findById(request: Request) = logger.info("Incoming request to get a store by id").let {
        request.getIdFromPath { Response(Status.OK).jsonBody(service.findById(it)) }
    }

    companion object : Loggable()

}
