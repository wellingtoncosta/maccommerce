package br.com.maccommerce.productservice

import br.com.maccommerce.productservice.domain.entity.Category
import br.com.maccommerce.productservice.domain.entity.Product
import br.com.maccommerce.productservice.domain.repository.CategoryRepository
import br.com.maccommerce.productservice.domain.repository.ProductRepository
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.koin.core.KoinComponent
import org.koin.core.get

object DatabaseMock : KoinComponent {

    fun startServer(port: Int): EmbeddedPostgres = EmbeddedPostgres.builder().setPort(port).start()

    fun insertCategory(category: Category) = get<CategoryRepository>().persist(category)

    fun insertProduct(product: Product) = get<ProductRepository>().persist(product)

}