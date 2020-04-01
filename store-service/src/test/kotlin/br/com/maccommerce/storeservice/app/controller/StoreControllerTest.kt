package br.com.maccommerce.storeservice.app.controller

import br.com.maccommerce.storeservice.app.extension.bodyAsJson
import br.com.maccommerce.storeservice.app.web.controller.StoreController
import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.service.StoreService
import br.com.maccommerce.storeservice.store
import io.azam.ulidj.ULID
import io.mockk.*
import org.http4k.core.Status
import org.http4k.format.Jackson
import org.http4k.routing.RoutedRequest
import org.http4k.routing.path
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

object StoreControllerTest : Spek({

    val service = mockk<StoreService>()

    val controller = StoreController(service)

    val request = mockk<RoutedRequest>(relaxed = true)

    describe("StoreController.save") {

        it("should save a new store") {
            every { request.bodyString() } returns Jackson.asJsonString(store)
            every { service.save(store) } returns store.copy(id = ULID.random())

            val result = controller.save(request)

            assertEquals(Status.CREATED, result.status)
            assertNotNull(result.bodyString())
            assertNotNull(result.bodyAsJson<Store>().id)

            verify { request.bodyString() }
            verify { service.save(store) }
        }

    }

    describe("StoreController.update") {

        it("should update a store") {
            val storeId = ULID.random()
            val storeWithId = store.copy(id = storeId)

            every { request.path("id") } returns storeId
            every { request.bodyString() } returns Jackson.asJsonString(storeWithId)
            every { service.update(storeId, storeWithId) } returns storeWithId

            val result = controller.update(request)

            assertEquals(Status.OK, result.status)
            assertEquals(storeWithId, Jackson.asA(result.bodyString(), Store::class))

            verify { request.bodyString() }
            verify { service.update(storeId, storeWithId) }
        }

    }

    describe("StoreController.delete") {

        it("should delete a store") {
            val storeId = ULID.random()

            every { request.path("id") } returns storeId
            every { service.delete(storeId) } just Runs

            val result = controller.delete(request)

            assertEquals(Status.NO_CONTENT, result.status)

            verify { request.bodyString() }
        }

    }

    describe("StoreController.findAll") {

        it("should get all stores") {
            every { service.findAll() } returns listOf()

            val result = controller.findAll()

            assertEquals(Status.OK, result.status)
            assertEquals(0, result.bodyAsJson<List<Store>>().size)

            verify { request.bodyString() }
        }

    }

    describe("StoreController.findById") {

        it("should find a store by id") {
            val storeId = ULID.random()
            val storeWithId = store.copy(id = storeId)

            every { request.path("id") } returns storeId
            every { service.findById(storeId) } returns storeWithId

            val result = controller.findById(request)

            assertEquals(Status.OK, result.status)
            assertEquals(storeWithId, result.bodyAsJson())

            verify { request.bodyString() }
        }

    }

})