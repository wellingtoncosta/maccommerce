package br.com.maccommerce.storeservice

import br.com.maccommerce.storeservice.DatabaseMock.insertStore
import br.com.maccommerce.storeservice.app.App
import br.com.maccommerce.storeservice.app.extension.bodyAsJson
import br.com.maccommerce.storeservice.app.web.entity.ApiExceptionResponse
import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.exception.ApiExceptionType
import io.azam.ulidj.ULID
import io.mockk.unmockkAll
import org.flywaydb.core.Flyway
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.MemoryRequest
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.format.Jackson
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object StoreTest : Spek({

    val embeddedPostgres = DatabaseMock.startServer(5433)

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { DependenciesMock.loadKoinProperfiesFromFile().also { App.start() } }

    afterGroup { unmockkAll().also { App.stop() } }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean().also { migrate() } } }

    describe("Store Tests") {

        val endpoint = "http://localhost:7000/stores"

        describe("Store.findAll") {

            it("should get all stores with empty result") {
                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Store>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isEmpty() }
            }

            it("should get all stores with one result") {
                insertStore(store)

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Store>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isNotEmpty() }
            }

            it("should get all stores with three results") {
                insertStore(store)
                insertStore(store)
                insertStore(store)

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Store>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.size == 3 }
            }

        }

        describe("Store.save") {

            it("should save a new store") {
                val store = store
                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.POST,
                    uri = Uri.of(endpoint),
                    body = Body(Jackson.asJsonString(store))
                )
                val response = client(request)
                val result = response.bodyAsJson<Store>()

                assertEquals(Status.CREATED, response.status)
                assertNotNull(result)
                assertNotNull(result.id)
                assertEquals(store.name, result.name)
                assertEquals(store.description, result.description)
            }

        }

        describe("Store.update") {

            it("should update a store") {
                val store = insertStore(store)
                val storeToUpdate = store.copy(name = "Updated", description = "Updated")

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/${store.id}"),
                    body = Body(Jackson.asJsonString(storeToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<Store>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(store.id, result.id)
                assertEquals(storeToUpdate.name, result.name)
                assertEquals(storeToUpdate.description, result.description)
            }

            it("should not update a store when id does not exit") {
                val store = insertStore(store)
                val storeToUpdate = store.copy(name = "Updated", description = "Updated")
                val invalidId = ULID.random()

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/$invalidId"),
                    body = Body(Jackson.asJsonString(storeToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<ApiExceptionResponse>()

                assertEquals(Status.NOT_FOUND, response.status)
                assertNotNull(result)
                assertEquals(ApiExceptionType.NOT_FOUND, result.type)
                assertEquals("Store with id $invalidId was not found.", result.message)
            }

        }

        describe("Store.delete") {

            it( "should delete a store") {
                val store = insertStore(store)

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.DELETE,
                    uri = Uri.of("$endpoint/${store.id}")
                )
                val response = client(request)

                assertEquals(Status.NO_CONTENT, response.status)
            }

        }

        describe("Store.findById") {

            it( "should find a store by id") {
                val store = insertStore(store)

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.GET,
                    uri = Uri.of("$endpoint/${store.id}")
                )
                val response = client(request)
                val result = response.bodyAsJson<Store>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(store, result)
            }

            it("should not find a store by id when id does not exit") {
                insertStore(store)
                val invalidId = ULID.random()

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.GET,
                    uri = Uri.of("$endpoint/$invalidId")
                )
                val response = client(request)
                val result = response.bodyAsJson<ApiExceptionResponse>()

                assertEquals(Status.NOT_FOUND, response.status)
                assertNotNull(result)
                assertEquals(ApiExceptionType.NOT_FOUND, result.type)
                assertEquals("Store with id $invalidId was not found.", result.message)
            }

        }

    }

})
