package org.jetbrains.dukat.compiler.tests.core

import kotlin.test.assertEquals

fun assertNormalizedContentEquals(expected: String, actual: String, message: String? = null) {
    assertEquals(
        expected.replace(System.getProperty("line.separator"), "\n"),
        actual,
        message
    )
}