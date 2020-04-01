package br.com.maccommerce.productservice.domain.service.impl

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.repository.CategoryRepository
import br.com.maccommerce.productservice.domain.service.CategoryService

class CategoryServiceImpl(
    repository: CategoryRepository
) : CategoryService, CrudServiceImpl<Category>(repository)
