import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceConventionsTests : StandardTests() {
    @Test
    fun testCallSignature() {
        assertContentEquals("interface/conventions/callSignature")
    }

    @Test
    fun testIndexSignature() {
        assertContentEquals("interface/conventions/indexSignature")
    }
}