import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class InterfaceMethodsTests : StandardTests() {
    @Test
    fun methods() {
        assertContentEquals("interface/methods/methods")
    }

}