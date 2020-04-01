package br.com.maccommerce.productservice

import br.com.maccommerce.productservice.DatabaseMock.insertCategory
import br.com.maccommerce.productservice.DatabaseMock.insertProduct
import br.com.maccommerce.productservice.app.App
import br.com.maccommerce.productservice.app.extension.bodyAsJson
import br.com.maccommerce.productservice.app.web.entity.ApiExceptionResponse
import br.com.maccommerce.productservice.app.web.entity.ProductRequest
import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.exception.ApiExceptionType
import io.azam.ulidj.ULID
import io.mockk.unmockkAll
import org.flywaydb.core.Flyway
import org.http4k.client.ApacheClient
import org.http4k.core.MemoryRequest
import org.http4k.core.Body
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

object ProductTest : Spek({

    val embeddedPostgres = DatabaseMock.startServer(5433)

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { DependenciesMock.loadKoinProperfiesFromFile().also { App.start() } }

    afterGroup { unmockkAll().also { App.stop() } }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean().also { migrate() } } }

    describe("Product Tests") {

        val endpoint = "http://localhost:7000/products"

        lateinit var category: Category

        beforeEachTest { category = insertCategory(Category(name = "Test", description = "Test")) }

        describe("Product.findAll") {

            it("should get all products with empty result") {
                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Product>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isEmpty() }
            }

            it("should get all products with one result") {
                insertProduct(Product(name = "Test", price = 99.99, category = category))

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Category>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.isNotEmpty() }
            }

            it("should get all categories with three results") {
                insertProduct(Product(name = "Test", price = 99.99, category = category))
                insertProduct(Product(name = "Test", price = 99.99, category = category))
                insertProduct(Product(name = "Test", price = 99.99, category = category))

                val client = ApacheClient()
                val request = Request(Method.GET, endpoint)
                val response = client(request)
                val result = response.bodyAsJson<List<Category>>()

                assertEquals(Status.OK, response.status)
                assertTrue { result.size == 3 }
            }

        }

        describe("Product.save") {

            it("should save a new product") {
                val product = ProductRequest(name = "Test", description = "Test", price = 99.99, categoryId = category.id!!)
                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.POST,
                    uri = Uri.of(endpoint),
                    body = Body(Jackson.asJsonString(product))
                )
                val response = client(request)
                val result = response.bodyAsJson<Product>()

                assertEquals(Status.CREATED, response.status)
                assertNotNull(result)
                assertNotNull(result.id)
                assertEquals(product.name, result.name)
                assertEquals(product.description, result.description)
                assertEquals(product.price, result.price)
            }

        }

        describe("Product.update") {

            it("should update a product") {
                val product = insertProduct(Product(name = "Test", price = 99.99, category = category))
                val productToUpdate = product.copy(name = "Update", description = "Updated")

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/${product.id}"),
                    body = Body(Jackson.asJsonString(productToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<Product>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(product.id, result.id)
                assertEquals(productToUpdate.name, result.name)
                assertEquals(productToUpdate.description, result.description)
                assertEquals(productToUpdate.price, result.price)
            }

            it("should not update a product when id does not exit") {
                val product = insertProduct(Product(name = "Test", price = 99.99, category = category))
                val productToUpdate = product.copy(name = "Update", description = "Updated")
                val invalidId = ULID.random()

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.PUT,
                    uri = Uri.of("$endpoint/$invalidId"),
                    body = Body(Jackson.asJsonString(productToUpdate))
                )
                val response = client(request)
                val result = response.bodyAsJson<ApiExceptionResponse>()

                assertEquals(Status.NOT_FOUND, response.status)
                assertNotNull(result)
                assertEquals(ApiExceptionType.NOT_FOUND, result.type)
                assertEquals("Entity with id $invalidId was not found.", result.message)
            }

        }

        describe("Product.delete") {

            it( "should delete a product") {
                val product = insertProduct(Product(name = "Test", price = 99.99, category = category))

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.DELETE,
                    uri = Uri.of("$endpoint/${product.id}")
                )
                val response = client(request)

                assertEquals(Status.NO_CONTENT, response.status)
            }

        }

        describe("Product.findById") {

            it( "should find a product by id") {
                val product = insertProduct(Product(name = "Test", price = 99.99, category = category))

                val client = ApacheClient()
                val request = MemoryRequest(
                    method = Method.GET,
                    uri = Uri.of("$endpoint/${product.id}")
                )
                val response = client(request)
                val result = response.bodyAsJson<Product>()

                assertEquals(Status.OK, response.status)
                assertNotNull(result)
                assertEquals(product, result)
            }

            it("should not find a product by id when id does not exit") {
                insertProduct(Product(name = "Test", price = 99.99, category = category))
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
