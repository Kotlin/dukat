package org.jetbrains.dukat.logger

import mu.KotlinLogging

class Logging(private val name: String) {

    val myLogger
        get() = KotlinLogging.logger(name)

    companion object {
        @JvmStatic
        @JvmName("logger")
        fun logger(name :String) = Logging(name)
    }

    fun debug(msg: () -> Any?) {
        myLogger.debug(msg)
    }

    fun debug(t: Throwable?, msg: () -> Any?) {
        myLogger.debug(t, msg)
    }

    fun debug(msg: String?) {
        myLogger.debug(msg)
    }

    fun debug(format: String?, arg: Any?) {
        myLogger.debug(format, arg)
    }

    fun debug(format: String?, arg1: Any?, arg2: Any?) {
        myLogger.debug(format, arg1, arg2)
    }

    fun trace(format: String?, vararg arguments: Any?) {
        myLogger.trace(format, arguments)
    }

    fun trace(msg: String?, t: Throwable?) {
        myLogger.trace(msg, t)
    }

    fun trace(msg: String?) {
        myLogger.trace(msg)
    }

    fun trace(format: String?, arg: Any?) {
        myLogger.trace(format, arg)
    }

    fun trace(format: String?, arg1: Any?, arg2: Any?) {
        myLogger.trace(format, arg1, arg2)
    }

    fun warn(msg: () -> Any?) {
        myLogger.warn(msg)
    }

    fun warn(t: Throwable?, msg: () -> Any?) {
        myLogger.warn(t, msg)
    }


    fun warn(msg: String?) {
        myLogger.warn(msg)
    }

    fun warn(format: String?, arg: Any?) {
        myLogger.warn(format, arg)
    }

    fun warn(format: String?, arg1: Any?, arg2: Any?) {
        myLogger.warn(format, arg1, arg2)
    }

    fun warn(format: String?, vararg arguments: Any?) {
        myLogger.warn(format, arguments)
    }

    fun warn(msg: String?, t: Throwable?) {
        myLogger.warn(msg, t)
    }

    fun error(msg: () -> Any?) {
        myLogger.error(msg)
    }

    fun error(t: Throwable?, msg: () -> Any?) {
        myLogger.error(t, msg)
    }

    fun error(msg: String?) {
        myLogger.error(msg)
    }

    fun error(format: String?, arg: Any?) {
        myLogger.error(format, arg)
    }

    fun error(format: String?, arg1: Any?, arg2: Any?) {
        myLogger.error(format, arg1, arg2)
    }

    fun error(format: String?, vararg arguments: Any?) {
        myLogger.error(format, arguments)
    }

    fun error(msg: String?, t: Throwable?) {
        myLogger.error(msg, t)
    }


    fun info(msg: () -> Any?) {
        myLogger.info(msg)
    }

    fun info(t: Throwable?, msg: () -> Any?) {
        myLogger.info(t, msg)
    }

    fun info(msg: String?) {
        myLogger.info(msg)
    }

    fun info(format: String?, arg: Any?) {
        myLogger.info(format, arg)
    }

    fun info(format: String?, arg1: Any?, arg2: Any?) {
        myLogger.info(format, arg1, arg2)
    }

    fun info(format: String?, vararg arguments: Any?) {
        myLogger.info(format, arguments)
    }

    fun info(msg: String?, t: Throwable?) {
        myLogger.info(msg, t)
    }
}