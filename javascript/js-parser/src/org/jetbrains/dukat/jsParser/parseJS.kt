package org.jetbrains.dukat.jsParser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.jsDeclarations.JSModuleDeclaration
import java.io.File

fun parseJS(fileName: String): JSModuleDeclaration {
    val source = Source.sourceFor(fileName, File(fileName).readText())
    val scriptEnv = ScriptEnvironment.builder().ecmaScriptVersion(Integer.MAX_VALUE).build()

    val baseNode = Parser(scriptEnv, source, ErrorManager.ThrowErrorManager()).parse()

    val scriptBody = baseNode.body

    for (line in scriptBody.statements) {
        println(line.lineNumber.toString() + ">\t" + line)

        if (line is ExpressionStatement) {
            val expression = line.expression

            if (expression.isAssignment) {

                if (expression is BinaryNode) {
                    println("\t\t" + expression.assignmentDest)
                }
            }
        }
    }

    return JSModuleDeclaration(fileName, IdentifierEntity("test"))
}
