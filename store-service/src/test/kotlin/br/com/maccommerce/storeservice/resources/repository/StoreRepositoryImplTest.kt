package br.com.maccommerce.storeservice.resources.repository

import br.com.maccommerce.storeservice.domain.exception.DatabaseException
import br.com.maccommerce.storeservice.store
import io.azam.ulidj.ULID
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.*

object StoreRepositoryImplTest : Spek({

    val repository = StoreRepositoryImpl()

    val embeddedPostgres: EmbeddedPostgres = EmbeddedPostgres.start()

    val dataSource = embeddedPostgres.postgresDatabase

    beforeGroup { Database.connect(dataSource) }

    afterGroup { embeddedPostgres.close() }

    beforeEachTest { Flyway.configure().run { dataSource(dataSource).load() }.apply { clean(); migrate() } }

    describe("StoreRepository.persist") {

        it("should persist a new store") {
            val result = repository.persist(store)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(repository.findAll().size, 1)
        }

    }

    describe("StoreRepository.update") {

        it("should update a store") {
            val saved = repository.persist(store)
            val updated = repository.update(saved.id!!, saved.copy(name = "Test 2", description = "Test 2"))

            assertNotNull(updated)
            assertNotNull(updated.id)
            assertEquals("Test 2", updated.name)
            assertEquals("Test 2", updated.description)
            assertEquals(repository.findAll().size, 1)
        }

        it("should not update a store when it does not exist") {
            val wrongId = ULID.random()
            val saved = repository.persist(store)

            assertFailsWith<DatabaseException>(message = "Category with id = $wrongId was not updated.") {
                repository.update(wrongId, saved.copy(name = "Test 2", description = "Test 2"))
            }

        }

    }

    describe("StoreRepository.delete") {

        it("should delete a store") {
            val saved = repository.persist(store)

            repository.delete(saved.id!!)

            assertTrue { repository.findAll().isEmpty() }
        }

    }

    describe("StoreRepository.findById") {

        it("should find a store by id") {
            val saved = repository.persist(store)

            assertNotNull(repository.findById(saved.id!!))
        }

        it("should not find a store by id") {
            repository.persist(store)

            assertNull(repository.findById(ULID.random()))
        }

    }

    describe("StoreRepository.findAll") {

        it("should list all stores with empty result") {
            assertTrue { repository.findAll().isEmpty() }
        }

        it("should list all stores with one result") {
            repository.persist(store)

            assertTrue { repository.findAll().size == 1 }
        }

        it("should list all stores with three results") {
            repository.persist(store)
            repository.persist(store)
            repository.persist(store)

            assertTrue { repository.findAll().size == 3 }
        }

    }

})
