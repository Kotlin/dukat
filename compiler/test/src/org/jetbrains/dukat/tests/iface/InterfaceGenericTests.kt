import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceGenericTests : StandardTests() {
    @Test
    fun testGenerics() {
        assertContentEquals("interface/generics/generics")
    }

    @Test
    fun testGenericsWithConstraint() {
        assertContentEquals("interface/generics/genericsWithConstraint")
    }

    @Test
    fun testGenericOptionalMethods() {
        assertContentEquals("interface/generics/genericOptionalMethods")
    }

}