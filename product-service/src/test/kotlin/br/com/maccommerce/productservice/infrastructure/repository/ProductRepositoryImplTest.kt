package br.com.maccommerce.productservice.infrastructure.repository

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.exception.DatabaseException
import br.com.maccommerce.productservice.infrastructure.entity.CategoryTable
import io.azam.ulidj.ULID
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

object ProductRepositoryImplTest : Spek({

    val repository = ProductRepositoryImpl()

    val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { Database.connect(dataSource) }

    afterGroup { embeddedPostgres.close() }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean(); migrate() } }

    fun insertCategory(category: Category) = transaction {
        val ulid = ULID.random()
        CategoryTable.insert {
            it[id] = ulid
            it[name] = category.name
            it[description] = category.description
        }.let { category.copy(id = ulid) }
    }

    describe("ProductRepository.persist") {

        it("should persist a new product") {
            val product = Product(
                name = "Test",
                description = "Test",
                price = 99.99,
                category = insertCategory(Category(name = "Test"))
            )

            val result = repository.persist(product)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(repository.findAll().size, 1)
        }

    }

    describe("ProductRepository.update") {

        it("should update a product") {
            val saved = repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            val updated = repository.update(saved.id!!, saved.copy(name = "Test 2", description = "Test 2", price = 100.00))

            assertNotNull(updated)
            assertNotNull(updated.id)
            assertEquals("Test 2", updated.name)
            assertEquals("Test 2", updated.description)
            assertEquals(100.00, updated.price)
            assertEquals(repository.findAll().size, 1)
        }

        it("should not update a product when it does not exist") {
            val wrongId = ULID.random()
            val saved = repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            assertFailsWith<DatabaseException>(message = "Produce with id = $wrongId was not updated.") {
                repository.update(wrongId, saved.copy(name = "Test 2", description = "Test 2", price = 100.00))
            }

        }

    }

    describe("ProductRepository.delete") {

        it("should delete a product") {
            val saved = repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            repository.delete(saved.id!!)

            assertTrue { repository.findAll().isEmpty() }
        }

    }

    describe("ProductRepository.findById") {

        it("should find a product by id") {
            val saved = repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            assertNotNull(repository.findById(saved.id!!))
        }

        it("should not find a product by id") {
            repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            assertNull(repository.findById(ULID.random()))
        }

    }

    describe("ProductRepository.findAll") {

        it("should list all products with empty result") {
            assertTrue { repository.findAll().isEmpty() }
        }

        it("should list all products with one result") {
            repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            assertTrue { repository.findAll().size == 1 }
        }

        it("should list all products with three results") {
            repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            repository.persist(
                Product(
                    name = "Test",
                    description = "Test",
                    price = 99.99,
                    category = insertCategory(Category(name = "Test"))
                )
            )

            assertTrue { repository.findAll().size == 3 }
        }

    }

})
