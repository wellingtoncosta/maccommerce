package br.com.maccommerce.productservice.infrastructure.repository

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.exception.DatabaseException
import io.azam.ulidj.ULID
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

object CategoryRepositoryImplTest : Spek({

    val repository = CategoryRepositoryImpl()

    val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { Database.connect(dataSource) }

    afterGroup { embeddedPostgres.close() }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean(); migrate() } }

    describe("CategoryRepository.persist") {

        it("should persist a new category") {
            val category = Category(name = "Test", description = "Test")

            val result = repository.persist(category)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(repository.findAll().size, 1)
        }

    }

    describe("CategoryRepository.update") {

        it("should update a category") {
            val saved = repository.persist(Category(name = "Test", description = "Test"))
            val updated = repository.update(saved.id!!, saved.copy(name = "Test 2", description = "Test 2"))

            assertNotNull(updated)
            assertNotNull(updated.id)
            assertEquals("Test 2", updated.name)
            assertEquals("Test 2", updated.description)
            assertEquals(repository.findAll().size, 1)
        }

        it("should not update a category when it does not exist") {
            val wrongId = ULID.random()
            val saved = repository.persist(Category(name = "Test", description = "Test"))

            assertFailsWith<DatabaseException>(message = "Category with id = $wrongId was not updated.") {
                repository.update(wrongId, saved.copy(name = "Test 2", description = "Test 2"))
            }

        }

    }

    describe("CategoryRepository.delete") {

        it("should delete a category") {
            val saved = repository.persist(Category(name = "Test", description = "Test"))

            repository.delete(saved.id!!)

            assertTrue { repository.findAll().isEmpty() }
        }

    }

    describe("CategoryRepository.findById") {

        it("should find a category by id") {
            val saved = repository.persist(Category(name = "Test", description = "Test"))

            assertNotNull(repository.findById(saved.id!!))
        }

        it("should not find a category by id") {
            repository.persist(Category(name = "Test", description = "Test"))

            assertNull(repository.findById(ULID.random()))
        }

    }

    describe("CategoryRepository.findAll") {

        it("should list all categories with empty result") {
            assertTrue { repository.findAll().isEmpty() }
        }

        it("should list all categories with one result") {
            repository.persist(Category(name = "Test", description = "Test"))

            assertTrue { repository.findAll().size == 1 }
        }

        it("should list all categories with three results") {
            repository.persist(Category(name = "Test", description = "Test"))
            repository.persist(Category(name = "Test", description = "Test"))
            repository.persist(Category(name = "Test", description = "Test"))

            assertTrue { repository.findAll().size == 3 }
        }

    }

})
