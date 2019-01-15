import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceVariablesTests : StandardTests() {
    @Test
    fun variables() {
        assertContentEquals("interface/variables/variables")
    }

    @Test
    fun withNumberAsName() {
        assertContentEquals("interface/variables/withNumberAsName")
    }

}