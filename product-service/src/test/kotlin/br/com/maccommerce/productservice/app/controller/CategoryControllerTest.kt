package br.com.maccommerce.productservice.app.controller

import br.com.maccommerce.productservice.app.extension.bodyAsJson
import br.com.maccommerce.productservice.app.web.controller.CategoryController
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.service.CategoryService
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

object CategoryControllerTest : Spek({

    val service = mockk<CategoryService>()

    val controller = CategoryController(service)

    val request = mockk<RoutedRequest>(relaxed = true)

    describe("CategoryController.save") {

        it("should save a new category") {
            val category = Category(name = "Test", description = "Test")

            every { request.bodyString() } returns Jackson.asJsonString(category)
            every { service.save(category) } returns category.copy(id = ULID.random())

            val result = controller.save(request)

            assertEquals(Status.CREATED, result.status)
            assertNotNull(result.bodyString())
            assertNotNull(result.bodyAsJson<Category>().id)

            verify { request.bodyString() }
            verify { service.save(category) }
        }

    }

    describe("CategoryController.update") {

        it("should update a category") {
            val categoryId = ULID.random()
            val category = Category(id = categoryId, name = "Test", description = "Test")

            every { request.path("id") } returns categoryId
            every { request.bodyString() } returns Jackson.asJsonString(category)
            every { service.update(categoryId, category) } returns category

            val result = controller.update(request)

            assertEquals(Status.OK, result.status)
            assertEquals(category, Jackson.asA(result.bodyString(), Category::class))

            verify { request.bodyString() }
            verify { service.update(categoryId, category) }
        }

    }

    describe("CategoryController.delete") {

        it("should delete a category") {
            val categoryId = ULID.random()

            every { request.path("id") } returns categoryId
            every { service.delete(categoryId) } just Runs

            val result = controller.delete(request)

            assertEquals(Status.NO_CONTENT, result.status)

            verify { request.bodyString() }
        }

    }

    describe("CategoryController.findAll") {

        it("should get all categories") {
            every { service.findAll() } returns listOf()

            val result = controller.findAll(request)

            assertEquals(Status.OK, result.status)
            assertEquals(0, result.bodyAsJson<List<Category>>().size)

            verify { request.bodyString() }
        }

    }

    describe("CategoryController.findById") {

        it("should find a category by id") {
            val categoryId = ULID.random()
            val category = Category(id = categoryId, name = "Test", description = "Test")

            every { request.path("id") } returns categoryId
            every { service.findById(categoryId) } returns category

            val result = controller.findById(request)

            assertEquals(Status.OK, result.status)
            assertEquals(category, result.bodyAsJson())

            verify { request.bodyString() }
        }

    }

})