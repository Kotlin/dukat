package org.jetbrains.dukat.js.parser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.declarations.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.JSModuleDeclaration
import org.jetbrains.dukat.js.declarations.JSParameterDeclaration
import java.io.File

private fun getBodyOfJSFile(fileName: String) : Block {
    val source = Source.sourceFor(fileName, File(fileName).readText())
    val scriptEnv = ScriptEnvironment.builder().ecmaScriptVersion(Integer.MAX_VALUE).build()

    val baseNode = Parser(scriptEnv, source, ErrorManager.ThrowErrorManager()).parse()

    return baseNode.body
}

private fun Expression.isExportsExpression() : Boolean {
    return when(toString()) {
        "module.exports" -> true
        "exports" -> true
        else -> false
    }
}

private fun Block.getExportsFromJSBody() : Expression? {
    for (line in statements) {
        if (line is ExpressionStatement) {
            val expression = line.expression

            if (expression.isAssignment) {
                if (expression is BinaryNode) {
                    if(expression.assignmentDest.isExportsExpression()) {
                        return expression.assignmentSource
                    }
                }
            }
        }
    }

    return null
}

private fun IdentNode.toJSParameterDeclaration() : JSParameterDeclaration {
    return JSParameterDeclaration(
            name = name,
            vararg = false //TODO
    )
}

private fun FunctionNode.toJSFunctionDeclaration() : JSFunctionDeclaration {
    val parameterDeclarations = mutableListOf<JSParameterDeclaration>()

    for(parameter in parameters) {
        parameterDeclarations.add(parameter.toJSParameterDeclaration())
    }

    return JSFunctionDeclaration(
            name = IdentifierEntity(name),
            parameters = parameterDeclarations
    )
}

private fun Expression.getFunctions() : List<JSFunctionDeclaration> {
    return when(this) {
        is FunctionNode -> listOf(this.toJSFunctionDeclaration())
        else -> emptyList()
    }
}

fun parseJS(moduleName: String, fileName: String): JSModuleDeclaration {
    val scriptBody = getBodyOfJSFile(fileName)

    val exports = scriptBody.getExportsFromJSBody()

    val functions : List<JSFunctionDeclaration> = exports?.getFunctions() ?: emptyList()

    return JSModuleDeclaration(
            name = IdentifierEntity(moduleName),
            fileName = fileName,
            functions = functions
    )
}
