import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class ClassVariablesTests : StandardTests() {
    @Test
    fun computedName() {
        assertContentEquals("class/variables/varComputedname")
    }

    @Test
    fun variables() {
        assertContentEquals("class/variables/variables")
    }

    @Test
    fun numberAsName() {
        assertContentEquals("class/variables/withNumberAsName")
    }

}