package br.com.maccommerce.productservice.domain.services

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.exception.NotFoundException
import br.com.maccommerce.productservice.domain.repository.CategoryRepository
import br.com.maccommerce.productservice.domain.service.impl.CategoryServiceImpl
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

object CategoryServiceImplTest : Spek({

    val repository = mockk<CategoryRepository>()

    val service = CategoryServiceImpl(repository)

    describe("CategoryService.save") {

        it("should save a new category") {
            val category = Category(name = "Test", description = "Test")
            val categoryId = ULID.random()

            every { repository.persist(category) } returns category.copy(id = categoryId)

            val result = service.save(category)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(category.copy(id = categoryId), result)

            verify { repository.persist(category) }
        }

    }

    describe("CategoryService.update") {

        it("should update a existing category") {
            val categoryId = ULID.random()
            val category = Category(id = categoryId, name = "Test", description = "Test")

            every { repository.findById(categoryId) } returns category
            every { repository.update(categoryId, category) } returns category

            val result = service.update(categoryId, category)

            assertNotNull(result)
            assertNotNull(result.id)
            assertEquals(category, result)

            verify { repository.findById(categoryId) }
            verify { repository.update(categoryId, category) }
        }

        it("should not update a category when it does not exist") {
            val categoryId = ULID.random()
            val category = Category(id = categoryId, name = "Test", description = "Test")

            every { repository.findById(categoryId) } returns null

            assertFailsWith<NotFoundException>(message = "Entity with id $categoryId was not found.") {
                service.update(categoryId, category)
            }

            verify { repository.findById(categoryId) }
            verify(exactly = 0) { repository.update(categoryId, category) }
        }

    }

    describe("CategoryService.delete") {

        it("should delete a category") {
            val categoryId = ULID.random()

            every { repository.delete(categoryId) } just Runs

            service.delete(categoryId)

            verify { repository.delete(categoryId) }
        }

    }

    describe("CategoryService.findAll") {

        it("should get all categories with empty result") {
            every { repository.findAll() } returns emptyList()

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isEmpty() }

            verify { repository.findAll() }
        }

        it("should get all categories with one result") {
            every { repository.findAll() } returns listOf(Category(id = ULID.random(), name = "Test", description = "Test"))

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 1 }

            verify { repository.findAll() }
        }

        it("should get all categories with three results") {
            every { repository.findAll() } returns listOf(
                Category(id = ULID.random(), name = "Test", description = "Test"),
                Category(id = ULID.random(), name = "Test 2", description = "Test 2"),
                Category(id = ULID.random(), name = "Test 3", description = "Test 3")
            )

            val result = service.findAll()

            assertNotNull(result)
            assertTrue { result.isNotEmpty() }
            assertTrue { result.size == 3 }

            verify { repository.findAll() }
        }

    }

    describe("CategoryService.findById") {

        it("should find a existing category by id") {
            val categoryId = ULID.random()
            val category = Category(id = categoryId, name = "Test", description = "Test")

            every { repository.findById(categoryId) } returns category

            val result = service.findById(categoryId)

            assertNotNull(result)
            assertEquals(category, result)

            verify { repository.findById(categoryId) }
        }

        it("should not find the category by id when it does not exist") {
            val categoryId = ULID.random()

            every { repository.findById(categoryId) } returns null

            assertFailsWith<NotFoundException>(message = "Entity with id $categoryId was not found.") {
                service.findById(categoryId)
            }

            verify { repository.findById(categoryId) }
        }

    }

})
