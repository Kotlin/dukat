import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceOptionsTests : StandardTests() {
    @Test
    fun testOptionalMethods() {
        assertContentEquals("interface/optional/optionalMethods")
    }

    @Test
    fun testOptionalMethodsWithOptionalFunctionType() {
        assertContentEquals("interface/optional/optionalMethodsWithOptionalFunctionType")
    }

    @Test
    fun testOptionalVariables() {
        assertContentEquals("interface/optional/optionalVariables")
    }

    @Test
    fun testOptionalVariablesAsFunctionType() {
        assertContentEquals("interface/optional/optionalVariablesAsFunctionType")
    }
    
}