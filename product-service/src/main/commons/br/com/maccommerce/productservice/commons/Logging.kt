package br.com.maccommerce.productservice.commons

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

interface Logger {

    fun debug(message: String)

    fun info(message: String)

    fun warning(message: String)

    fun error(message: String, throwable: Throwable? = null)

}

class LoggerImpl(target: KClass<*>) : Logger {

    private val logger = LoggerFactory.getLogger(target.java)

    override fun debug(message: String) = logger.debug(message)

    override fun info(message: String) = logger.info(message)

    override fun warning(message: String) = logger.warn(message)

    override fun error(message: String, throwable: Throwable?) = logger.error(message, throwable)

}

open class Loggable {

    protected val logger by lazy { LoggerImpl(this::class) }

}
