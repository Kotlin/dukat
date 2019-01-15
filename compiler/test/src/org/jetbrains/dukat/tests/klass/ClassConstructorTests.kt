import org.jetbrains.dukat.tests.StandardTests
import org.junit.Test

class ClassConstructorTests : StandardTests() {
    @Test
    fun simple() {
        assertContentEquals("class/constructor/simple")
    }

    @Test
    fun withPropertyDeclaration() {
        assertContentEquals("class/constructor/withPropertyDeclaration")
    }

}