import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class InterfaceGenericTests : StandardTests() {
    @Test
    fun generics() {
        assertContentEquals("interface/generics/generics")
    }

    @Test
    fun genericsWithConstraint() {
        assertContentEquals("interface/generics/genericsWithConstraint")
    }

    @Test
    fun genericOptionalMethods() {
        assertContentEquals("interface/generics/genericOptionalMethods")
    }

}