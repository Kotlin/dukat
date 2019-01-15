import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test


class InterfaceInheritanceTests : StandardTests() {
    @Test
    fun simple() {
        assertContentEquals("interface/inheritance/simple")
    }

    @Test
    fun withGeneric() {
        assertContentEquals("class/inheritance/withGeneric")
    }

}