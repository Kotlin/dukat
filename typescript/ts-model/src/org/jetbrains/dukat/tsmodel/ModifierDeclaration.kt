package org.jetbrains.dukat.tsmodel

import org.jetbrains.dukat.astCommon.Entity

enum class ModifierDeclaration : Entity {
    DECLARE_KEYWORD,
    STATIC_KEYWORD,
    EXPORT_KEYWORD,
    DEFAULT_KEYWORD,

    SYNTH_EXPORT_ASSIGNMENT,

    SYNTH_IMMUTABLE
}