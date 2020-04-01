package br.com.maccommerce.productservice.domain.services

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.exception.NotFoundException
import br.com.maccommerce.productservice.domain.repository.ProductRepository
import br.com.maccommerce.productservice.domain.service.impl.ProductServiceImpl
import io.azam.ulidj.ULID
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object ProductServiceImplTest : Spek({

    val repository = mockk<ProductRepository>()

    val service = ProductServiceImpl(repository)

    describe("ProductService.save") {

        it("should save a new product") {
            val productId = ULID.random()
            val product = Product(
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { repository.persist(product) } returns product.copy(id = productId)

            val result = service.save(product)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(product.copy(id = productId), result)

            verify { repository.persist(product) }
        }

    }

    describe("ProductService.update") {

        it("should update a existing product") {
            val productId = ULID.random()
            val product = Product(
                id = productId,
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { repository.findById(productId) } returns product
            every { repository.update(productId, product) } returns product

            val result = service.update(productId, product)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(product, result)

            verify { repository.findById(productId) }
            verify { repository.update(productId, product) }
        }

        it("should not update a product when it does not exist") {
            val productId = ULID.random()
            val product = Product(
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { repository.findById(productId) } returns null

            assertFailsWith<NotFoundException>(message = "Entity with id $productId was not found.") {
                service.update(productId, product)
            }

            verify { repository.findById(productId) }
            verify(exactly = 0) { repository.update(productId, product) }
        }

    }

    describe("ProductService.delete") {

        it("should delete a product") {
            val productId = ULID.random()

            every { repository.delete(productId) } just Runs

            service.delete(productId)

            verify { repository.delete(productId) }
        }

    }

    describe("ProductService.findAll") {

        it("should get all products with empty result") {
            every { repository.findAll() } returns emptyList()

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isEmpty() }

            verify { repository.findAll() }
        }

        it("should get all products with one result") {
            every { repository.findAll() } returns listOf(
                Product(
                    id = ULID.random(),
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = Category(name = "Test")
                )
            )

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 1 }

            verify { repository.findAll() }
        }

        it("should get all products with three results") {
            every { repository.findAll() } returns listOf(
                Product(
                    id = ULID.random(),
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = Category(name = "Test")
                ),
                Product(
                    id = ULID.random(),
                    name = "Test 2",
                    description = "Test 2",
                    price = 99.99,
                    category = Category(name = "Test")
                ),
                Product(
                    id = ULID.random(),
                    name = "Test 3",
                    description = "Test 3",
                    price = 99.99,
                    category = Category(name = "Test")
                )
            )

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 3 }

            verify { repository.findAll() }
        }

    }

    describe("ProductService.findById") {

        it("should find a existing product by id") {
            val productId = ULID.random()
            val product = Product(
                id = ULID.random(),
                name = "Test",
                description = "Test",
                price = 99.99,
                category = Category(name = "Test")
            )

            every { repository.findById(productId) } returns product

            val result = service.findById(productId)

            assertNotNull(result)
            assertEquals(product, result)

            verify { repository.findById(productId) }
        }

        it("should not find the product by id when it does not exist") {
            val productId = ULID.random()

            every { repository.findById(productId) } returns null

            assertFailsWith<NotFoundException>(message = "Entity with id $productId was not found.") {
                service.findById(productId)
            }

            verify { repository.findById(productId) }
        }

    }

})
