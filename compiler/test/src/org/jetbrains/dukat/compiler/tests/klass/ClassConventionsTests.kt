import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class ClassConventionsTests : StandardTests() {
    @Test
    fun indexSignature() {
        assertContentEquals("class/conventions/indexSignature")
    }
}