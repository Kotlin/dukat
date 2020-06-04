package org.jetbrains.dukat.astModel.expressions.operators

enum class BinaryOperatorModel {
    PLUS, MINUS, MULT, DIV, MOD,
    ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MULT_ASSIGN, DIV_ASSIGN, MOD_ASSIGN,
    AND, OR,
    EQ, NOT_EQ, REF_EQ, REF_NOT_EQ,
    LT, GT, LE, GE,
    BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT
}