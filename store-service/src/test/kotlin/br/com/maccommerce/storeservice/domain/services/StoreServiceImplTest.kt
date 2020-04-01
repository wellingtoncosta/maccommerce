package br.com.maccommerce.storeservice.domain.services

import br.com.maccommerce.storeservice.domain.exception.NotFoundException
import br.com.maccommerce.storeservice.domain.repository.StoreRepository
import br.com.maccommerce.storeservice.domain.service.impl.StoreServiceImpl
import br.com.maccommerce.storeservice.store
import br.com.maccommerce.storeservice.storeWithId
import io.azam.ulidj.ULID
import io.mockk.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

object StoreServiceImplTest : Spek({

    val repository = mockk<StoreRepository>()

    val service = StoreServiceImpl(repository)

    describe("StoreService.save") {

        it("should save a new store") {
            val storeId = ULID.random()

            every { repository.persist(store) } returns store.copy(id = storeId)

            val result = service.save(store)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(store.copy(id = storeId), result)

            verify { repository.persist(store) }
        }

    }

    describe("StoreService.update") {

        it("should update a existing store") {
            val storeId = ULID.random()
            val storeWithId = store.copy(id = storeId)

            every { repository.findById(storeId) } returns storeWithId
            every { repository.update(storeId, storeWithId) } returns storeWithId

            val result = service.update(storeId, storeWithId)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(storeWithId, result)

            verify { repository.findById(storeId) }
            verify { repository.update(storeId, storeWithId) }
        }

        it("should not update a store when it does not exist") {
            val storeId = ULID.random()
            val storeWithId = store.copy(id = storeId)

            every { repository.findById(storeId) } returns null

            assertFailsWith<NotFoundException>(message = "Store with id $storeId was not found.") {
                service.update(storeId, storeWithId)
            }

            verify { repository.findById(storeId) }
            verify(exactly = 0) { repository.update(storeId, storeWithId) }
        }

    }

    describe("StoreService.delete") {

        it("should delete a store") {
            val categoryId = ULID.random()

            every { repository.delete(categoryId) } just Runs

            service.delete(categoryId)

            verify { repository.delete(categoryId) }
        }

    }

    describe("StoreService.findAll") {

        it("should get all stores with empty result") {
            every { repository.findAll() } returns emptyList()

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isEmpty() }

            verify { repository.findAll() }
        }

        it("should get all stores with one result") {
            every { repository.findAll() } returns listOf(storeWithId)

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 1 }

            verify { repository.findAll() }
        }

        it("should get all stores with three results") {
            every { repository.findAll() } returns listOf(
                storeWithId, storeWithId, storeWithId
            )

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 3 }

            verify { repository.findAll() }
        }

    }

    describe("StoreService.findById") {

        it("should find a existing store by id") {
            val storeId = ULID.random()
            val storeWithId = store.copy(id = storeId)

            every { repository.findById(storeId) } returns storeWithId

            val result = service.findById(storeId)

            assertNotNull(result)
            assertEquals(storeWithId, result)

            verify { repository.findById(storeId) }
        }

        it("should not find the store by id when it does not exist") {
            val storeId = ULID.random()

            every { repository.findById(storeId) } returns null

            assertFailsWith<NotFoundException>(message = "Store with id $storeId was not found.") {
                service.findById(storeId)
            }

            verify { repository.findById(storeId) }
        }

    }

})
