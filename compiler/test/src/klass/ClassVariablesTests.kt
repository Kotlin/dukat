import org.junit.Test

class ClassVariablesTests : StandardTests() {
    @Test
    fun testComputedName() {
        assertContentEquals("class/variables/varComputedname")
    }

    @Test
    fun testVariables() {
        assertContentEquals("class/variables/variables")
    }

    @Test
    fun withNumberAsName() {
        assertContentEquals("class/variables/withNumberAsName")
    }

}