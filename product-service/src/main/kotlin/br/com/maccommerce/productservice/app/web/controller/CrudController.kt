package br.com.maccommerce.productservice.app.web.controller

import org.http4k.core.Request
import org.http4k.core.Response

interface CrudController {

    fun save(request: Request): Response

    fun update(request: Request): Response

    fun delete(request: Request): Response

    fun findAll(request: Request): Response

    fun findById(request: Request): Response

}
