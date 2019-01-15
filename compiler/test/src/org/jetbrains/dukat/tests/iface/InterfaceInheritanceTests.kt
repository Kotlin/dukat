import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test


class InterfaceInheritanceTests : StandardTests() {
    @Test
    fun testSimple() {
        assertContentEquals("interface/inheritance/simple")
    }

    @Test
    fun testWithGeneric() {
        assertContentEquals("class/inheritance/withGeneric")
    }

}