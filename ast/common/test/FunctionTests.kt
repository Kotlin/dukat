import org.jetbrains.dukat.ast.FileResolver
import org.jetbrains.dukat.ast.compile
import org.jetbrains.dukat.ast.createTranslator
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionTests {

    companion object {
        var translator = createTranslator()
    }

    private fun assertContentEquals(fileNameSource: String, fileNameTarget: String) {
        assertEquals(
                compile(fileNameSource, FunctionTests.translator),
                FileResolver().resolve(fileNameTarget).trimEnd()
        )
    }

    @Test
    fun testMe() {
        assertContentEquals("./common/test/data/functions.d.ts", "./common/test/data/functions.dukat")
    }

}