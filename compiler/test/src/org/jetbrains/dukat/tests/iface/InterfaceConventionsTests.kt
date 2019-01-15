import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class InterfaceConventionsTests : StandardTests() {
    @Test
    fun signature() {
        assertContentEquals("interface/conventions/callSignature")
    }

    @Test
    fun indexSignature() {
        assertContentEquals("interface/conventions/indexSignature")
    }
}