import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class ClassConventionsTests : StandardTests() {
    @Test
    fun testIndexSignature() {
        assertContentEquals("class/conventions/indexSignature")
    }
}