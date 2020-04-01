package br.com.maccommerce.storeservice

import br.com.maccommerce.storeservice.domain.entity.Store
import io.azam.ulidj.ULID

val store: Store get() = Store(
    name = "Test",
    description = "Test",
    address = "Test",
    number = "999",
    postalCode = "Test"
)

val storeWithId: Store get() = Store(
    id = ULID.random(),
    name = "Test",
    description = "Test",
    address = "Test",
    number = "999",
    postalCode = "Test"
)