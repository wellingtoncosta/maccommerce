package br.com.maccommerce.storeservice

import br.com.maccommerce.storeservice.app.App
import br.com.maccommerce.storeservice.app.config.appModules
import io.mockk.every
import io.mockk.mockkObject
import org.koin.core.context.startKoin

object DependenciesMock {

    fun loadKoinProperfiesFromFile() {
        mockkObject(App, recordPrivateCalls = true)

        every { App["setupKoin"]() } answers {
            startKoin {
                printLogger()
                fileProperties()
                modules(appModules)
            }
        }
    }

}