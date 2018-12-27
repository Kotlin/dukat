import org.jetbrains.dukat.compiler.FileResolver
import org.jetbrains.dukat.compiler.Translator
import org.jetbrains.dukat.compiler.compile
import org.jetbrains.dukat.compiler.createV8Translator
import org.junit.BeforeClass
import kotlin.test.Test
import kotlin.test.assertEquals


class FunctionTests {

    companion object {
        val translator: Translator

        init {
            translator = createV8Translator()
        }

        @BeforeClass
        @JvmStatic
        fun setup() {
        }

        @BeforeClass
        @JvmStatic
        fun teardown() {
            //translator.release()
        }
    }


    private fun assertContentEquals(fileNameSource: String, fileNameTarget: String) {

        assertEquals(
                compile(fileNameSource, translator),
                FileResolver().resolve(fileNameTarget).trimEnd()
        )
    }

    @Test
    fun testFunctions() {
        assertContentEquals("./test/data/functions.d.ts", "./test/data/functions.d.kt")
    }

}