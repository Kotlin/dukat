import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test


class ClassInheritanceTests : StandardTests() {
    @Test
    fun overrides() {
        assertContentEquals("class/inheritance/overrides")
    }

    @Test
    fun simple() {
        assertContentEquals("class/inheritance/simple")
    }

    @Test
    fun withGeneric() {
        assertContentEquals("class/inheritance/withGeneric")
    }

}