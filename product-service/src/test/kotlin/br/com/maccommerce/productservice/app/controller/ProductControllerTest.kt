package br.com.maccommerce.productservice.app.controller

import br.com.maccommerce.productservice.app.extension.bodyAsJson
import br.com.maccommerce.productservice.app.web.controller.ProductController
import br.com.maccommerce.productservice.app.web.entity.ProductRequest
import br.com.maccommerce.productservice.app.web.entity.toProduct
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.service.ProductService
import io.azam.ulidj.ULID
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.RoutedRequest
import org.http4k.routing.path
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object ProductControllerTest : Spek({

    val service = mockk<ProductService>()

    val controller = ProductController(service)

    val request = mockk<RoutedRequest>(relaxed = true)

    describe("ProductController.save") {

        it("should save a new product") {
            val productRequest = ProductRequest(
                name = "Test",
                description = "Test",
                price = 99.99,
                categoryId = ULID.random()
            )

            val product = productRequest.toProduct()

            every { request.bodyString() } returns Jackson.asJsonString(productRequest)
            every { service.save(product) } returns product.copy(id = ULID.random())

            val result = controller.save(request)

            assertEquals(Status.CREATED, result.status)
            assertNotNull(result.bodyString())
            assertNotNull(result.bodyAsJson<Product>().id)

            verify { request.bodyString() }
            verify { service.save(product) }
        }

    }

    describe("ProductController.update") {

        it("should update a product") {
            val productId = ULID.random()
            val product = Product(
                id = productId,
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { request.path("id") } returns productId
            every { request.bodyString() } returns Jackson.asJsonString(product)
            every { service.update(productId, product) } returns product

            val result = controller.update(request)

            assertEquals(Status.OK, result.status)
            assertEquals(product, Jackson.asA(result.bodyString(), Product::class))

            verify { request.bodyString() }
            verify { service.update(productId, product) }
        }

    }

    describe("ProductController.delete") {

        it("should delete a product") {
            val productId = ULID.random()

            every { request.path("id") } returns productId
            every { service.delete(productId) } just Runs

            val result = controller.delete(request)

            assertEquals(Status.NO_CONTENT, result.status)

            verify { request.bodyString() }
        }

    }

    describe("ProductController.findAll") {

        it("should get all products") {
            every { service.findAll() } returns listOf()

            val result = controller.findAll(request)

            assertEquals(Status.OK, result.status)
            assertEquals(0, result.bodyAsJson<List<Product>>().size)

            verify { request.bodyString() }
        }

    }

    describe("ProductController.findById") {

        it("should find a product by id") {
            val productId = ULID.random()
            val product = Product(
                id = productId,
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { request.path("id") } returns productId
            every { service.findById(productId) } returns product

            val result = controller.findById(request)

            assertEquals(Status.OK, result.status)
            assertEquals(product, result.bodyAsJson())

            verify { request.bodyString() }
        }

    }

})