package org.jetbrains.dukat.compiler.tests.core

import kotlinx.serialization.Serializable

@Serializable
internal data class ReportJson(val outputs: List<String>)