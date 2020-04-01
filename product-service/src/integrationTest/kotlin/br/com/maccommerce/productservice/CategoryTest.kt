package br.com.maccommerce.productservice

import br.com.maccommerce.productservice.DatabaseMock.insertCategory
import br.com.maccommerce.productservice.app.App
import br.com.maccommerce.productservice.app.extension.bodyAsJson
import br.com.maccommerce.productservice.app.web.entity.ApiExceptionResponse
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.exception.ApiExceptionType
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

object CategoryTest : Spek({

    val embeddedPostgres = DatabaseMock.startServer(5433)

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { DependenciesMock.loadKoinProperfiesFromFile().also { App.start() } }

    afterGroup { unmockkAll().also { App.stop() } }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean().also { migrate() } } }

    describe("Category Tests") {

        val endpoint = "http://localhost:7000/categories"

        describe("Category.findAll") {

            it("should get all categories with empty result") {
                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Category>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isEmpty() }
            }

            it("should get all categories with one result") {
                insertCategory(Category(name = "Test", description = "Test"))

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Category>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isNotEmpty() }
            }

            it("should get all categories with three results") {
                insertCategory(Category(name = "Test", description = "Test"))
                insertCategory(Category(name = "Test", description = "Test"))
                insertCategory(Category(name = "Test", description = "Test"))

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Category>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.size == 3 }
            }

        }

        describe("Category.save") {

            it("should save a new category") {
                val category = Category(name = "Test", description = "Test")
                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.POST,
                    uri = Uri.of(endpoint),
                    body = Body(Jackson.asJsonString(category))
                )
                val response = client(request)
                val result = response.bodyAsJson<Category>()

                assertEquals(Status.CREATED, response.status)
                assertNotNull(result)
                assertNotNull(result.id)
                assertEquals(category.name, result.name)
                assertEquals(category.description, result.description)
            }

        }

        describe("Category.update") {

            it("should update a category") {
                val category = insertCategory(Category(name = "Test", description = "Test"))
                val categoryToUpdate = category.copy(name = "Update", description = "Updated")

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/${category.id}"),
                    body = Body(Jackson.asJsonString(categoryToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<Category>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(category.id, result.id)
                assertEquals(categoryToUpdate.name, result.name)
                assertEquals(categoryToUpdate.description, result.description)
            }

            it("should not update a category when id does not exit") {
                val category = insertCategory(Category(name = "Test", description = "Test"))
                val categoryToUpdate = category.copy(name = "Update", description = "Updated")
                val invalidId = ULID.random()

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/$invalidId"),
                    body = Body(Jackson.asJsonString(categoryToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<ApiExceptionResponse>()

                assertEquals(Status.NOT_FOUND, response.status)
                assertNotNull(result)
                assertEquals(ApiExceptionType.NOT_FOUND, result.type)
                assertEquals("Entity with id $invalidId was not found.", result.message)
            }

        }

        describe("Category.delete") {

            it( "should delete a category") {
                val category = insertCategory(Category(name = "Test", description = "Test"))

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.DELETE,
                    uri = Uri.of("$endpoint/${category.id}")
                )
                val response = client(request)

                assertEquals(Status.NO_CONTENT, response.status)
            }

        }

        describe("Category.findById") {

            it( "should find a category by id") {
                val category = insertCategory(Category(name = "Test", description = "Test"))

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.GET,
                    uri = Uri.of("$endpoint/${category.id}")
                )
                val response = client(request)
                val result = response.bodyAsJson<Category>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(category, result)
            }

            it("should not find a category by id when id does not exit") {
                insertCategory(Category(name = "Test", description = "Test"))
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
                assertEquals("Entity with id $invalidId was not found.", result.message)
            }

        }

    }

})
