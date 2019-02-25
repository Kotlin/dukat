import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class ClassGenericTests : StandardTests() {
    @Test
    fun generics() {
        assertContentEquals("class/generics/generics")
    }

    @Test
    fun genericsWithConstraint() {
        assertContentEquals("class/generics/genericsWithConstraint")
    }

}