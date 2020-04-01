package br.com.maccommerce.storeservice

import br.com.maccommerce.storeservice.domain.entity.Store
import br.com.maccommerce.storeservice.domain.repository.StoreRepository
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres
import org.koin.core.KoinComponent
import org.koin.core.get

object DatabaseMock : KoinComponent {

    fun startServer(port: Int): EmbeddedPostgres = EmbeddedPostgres.builder().setPort(port).start()

    fun insertStore(store: Store) = get<StoreRepository>().persist(store)

}