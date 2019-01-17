package withValues

external enum class SomeEnum {
    Foo /* = 1 */,
    Bar,
    `$` /* = 2 */,
    `BAZ$`,
    UnaryExpressionValue /* = -123 */,
    ExpressionValue /* = -123 + 23 */
}
external enum class AnotherEnum {
    StringValue /* = "my string value" */,
    UnaryExpressionValue /* = -123 */
}
