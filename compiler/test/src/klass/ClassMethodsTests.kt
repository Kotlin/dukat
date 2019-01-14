import org.junit.Test

class ClassMethodsTests : StandardTests() {
    @Test
    fun testMethods() {
        assertContentEquals("class/methods/methods")
    }


    @Test
    fun testWithComputedName() {
        assertContentEquals("class/methods/methodWithComputedName")
    }

}