import org.junit.Test

class ClassConstructorTests : StandardTests() {
    @Test
    fun testSimple() {
        assertContentEquals("class/constructor/simple")
    }

    @Test
    fun testWithPropertyDeclaration() {
        assertContentEquals("class/constructor/withPropertyDeclaration")
    }

}