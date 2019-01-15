import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceOverrideTests : StandardTests() {
    @Test
    fun tesAnyMembers() {
        assertContentEquals("interface/override/anyMembers")
    }

    @Test
    fun testInModule() {
        assertContentEquals("interface/override/inModule")
    }

    @Test
    fun testJustOverload() {
        assertContentEquals("interface/override/justOverload")
    }

    @Test
    fun testSimple() {
        assertContentEquals("interface/override/simple")
    }
}