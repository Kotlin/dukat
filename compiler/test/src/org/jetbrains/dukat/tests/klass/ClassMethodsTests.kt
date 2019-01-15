import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class ClassMethodsTests : StandardTests() {
    @Test
    fun methods() {
        assertContentEquals("class/methods/methods")
    }

    @Test
    fun withComputedName() {
        assertContentEquals("class/methods/methodWithComputedName")
    }

}