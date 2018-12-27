import org.jetbrains.dukat.compiler.Translator
import org.jetbrains.dukat.compiler.compile
import org.jetbrains.dukat.compiler.createV8Translator
import org.junit.BeforeClass
import java.io.File
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

        val resourceDirectory = File("./test/data")

        assertEquals(
                compile(resourceDirectory.resolve(fileNameSource).absolutePath, translator),
                resourceDirectory.resolve(fileNameTarget).readText()
        )
    }

    @Test
    fun testFunctions() {
        assertContentEquals("functions.d.ts", "functions.d.kt")
    }

}