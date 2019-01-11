import org.junit.Test

class GenericsTests : StandardTests() {
    @Test
    fun testGenerics() {
        assertContentEquals("topLevelMembers/generics/generics")
    }

    @Test
    fun testGenericsWithConstraint() {
        assertContentEquals("topLevelMembers/generics/genericsWithConstraint")
    }
}