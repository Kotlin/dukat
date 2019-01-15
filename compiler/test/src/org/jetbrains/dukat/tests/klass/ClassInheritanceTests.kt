import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test


class ClassInheritanceTests : StandardTests() {
    @Test
    fun testOverrides() {
        assertContentEquals("class/inheritance/overrides")
    }

    @Test
    fun testSimple() {
        assertContentEquals("class/inheritance/simple")
    }

    @Test
    fun testWithGeneric() {
        assertContentEquals("class/inheritance/withGeneric")
    }

}