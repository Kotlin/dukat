import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceOverrideTests : StandardTests() {
    @Test
    fun anyMembers() {
        assertContentEquals("interface/override/anyMembers")
    }

    @Test
    fun inModule() {
        assertContentEquals("interface/override/inModule")
    }

    @Test
    fun justOverload() {
        assertContentEquals("interface/override/justOverload")
    }

    @Test
    fun simple() {
        assertContentEquals("interface/override/simple")
    }
}