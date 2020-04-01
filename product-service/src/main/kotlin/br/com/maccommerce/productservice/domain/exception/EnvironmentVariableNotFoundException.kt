package br.com.maccommerce.productservice.domain.exception

import java.lang.RuntimeException

class EnvironmentVariableNotFoundException(environmentVariableName: String) : RuntimeException(
    "Environment variable '$environmentVariableName' is not present. Check your system and try again."
)