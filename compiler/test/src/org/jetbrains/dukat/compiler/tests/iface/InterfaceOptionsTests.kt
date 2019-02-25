import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class InterfaceOptionsTests : StandardTests() {
    @Test
    fun optionalMethods() {
        assertContentEquals("interface/optional/optionalMethods")
    }

    @Test
    fun optionalMethodsWithOptionalFunctionType() {
        assertContentEquals("interface/optional/optionalMethodsWithOptionalFunctionType")
    }

    @Test
    fun optionalVariables() {
        assertContentEquals("interface/optional/optionalVariables")
    }

    @Test
    fun optionalVariablesAsFunctionType() {
        assertContentEquals("interface/optional/optionalVariablesAsFunctionType")
    }
    
}