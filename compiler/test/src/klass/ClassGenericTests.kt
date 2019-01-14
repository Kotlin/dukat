import org.junit.Test

class ClassGenericTests : StandardTests() {
    @Test
    fun testGenerics() {
        assertContentEquals("class/generics/generics")
    }

    @Test
    fun testGenericsWithConstraint() {
        assertContentEquals("class/generics/genericsWithConstraint")
    }

}