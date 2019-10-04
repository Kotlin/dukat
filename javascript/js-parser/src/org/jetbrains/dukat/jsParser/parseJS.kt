package org.jetbrains.dukat.jsParser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import java.io.File


fun parseJS(fileName: String) {
    val sourceCode = File(fileName).readText()

    val source = Source.sourceFor(fileName, sourceCode)

    val parser = Parser(ScriptEnvironment.builder().ecmaScriptVersion(Integer.MAX_VALUE).build(), source, ErrorManager.ThrowErrorManager())

    val baseNode = parser.parse()

    val scriptBody = baseNode.body

    for (line in scriptBody.statements) {
        println(line.lineNumber.toString() + " <" + line.javaClass.name + ">\t" + line)

        if (line is ExpressionStatement) {
            val expression = line.expression

            if (expression.isAssignment) {

                if (expression is BinaryNode) {
                    println("\t" + expression.assignmentDest)
                }
            }
        }
    }
}
