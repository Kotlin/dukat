package org.jetbrains.dukat.js.parser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.js.declarations.*
import org.jetbrains.dukat.js.declarations.export.JSExportDeclaration
import org.jetbrains.dukat.js.declarations.export.JSInlineExportDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSTopLevelDeclaration
import java.io.File

private fun getBodyOfJSFile(fileName: String) : Block {
    val source = Source.sourceFor(fileName, File(fileName).readText())
    val scriptEnv = ScriptEnvironment.builder().ecmaScriptVersion(Integer.MAX_VALUE).build()

    val baseNode = Parser(scriptEnv, source, ErrorManager.ThrowErrorManager()).parse()

    return baseNode.body
}


private fun IdentNode.toJSParameterDeclaration() : JSParameterDeclaration {
    return JSParameterDeclaration(
            name = name,
            vararg = false //TODO
    )
}

private fun FunctionNode.toTopLevelDeclaration() : JSFunctionDeclaration {
    val parameterDeclarations = mutableListOf<JSParameterDeclaration>()

    for(parameter in parameters) {
        parameterDeclarations.add(parameter.toJSParameterDeclaration())
    }

    return JSFunctionDeclaration(
            name = name,
            parameters = parameterDeclarations
    )
}

private fun FunctionNode.toExportDeclaration() : JSExportDeclaration {
    val functionDeclaration = this.toTopLevelDeclaration()

    return JSInlineExportDeclaration(
            name = functionDeclaration.name,
            declaration = functionDeclaration
    )
}

private fun Expression.toTopLevelDeclaration() : JSExportDeclaration? {
    return when(this) {
        is FunctionNode -> this.toExportDeclaration()
        else -> {println("Unsupported!");null}
    }
}


private fun Expression.isExportsExpression() : Boolean {
    return when(toString()) {
        "module.exports" -> true
        "exports" -> true
        else -> false
    }
}

private fun Block.getExportDeclarations() : List<JSExportDeclaration> {
    val exportDeclarations = mutableListOf<JSExportDeclaration>()

    for (statement in statements) {
        if (statement is ExpressionStatement) {
            val expression = statement.expression

            if (expression.isAssignment) {
                if (expression is BinaryNode) {
                    if(expression.assignmentDest.isExportsExpression()) {
                        val exportDeclaration = expression.assignmentSource.toTopLevelDeclaration()

                        if(exportDeclaration != null) {
                            exportDeclarations.add(exportDeclaration)
                        }
                    }
                }
            }
        }
    }

    return exportDeclarations
}

private fun Block.getTopLevelDeclarations() : Map<String, JSTopLevelDeclaration> {
    val topLevelDeclarations = mutableMapOf<String, JSTopLevelDeclaration>()

    /*
    for (statement in statements) {
        if (statement is ExpressionStatement) {
            val expression = statement.expression

            if (expression.isAssignment) {
                if (expression is BinaryNode) {
                    if(expression.assignmentDest.isExportsExpression()) {
                        //TODO
                    }
                }
            }
        }
    }
    */

    return topLevelDeclarations
}


fun parseJS(moduleName: String, fileName: String): JSModuleDeclaration {
    val scriptBody = getBodyOfJSFile(fileName)

    val topLevelDeclarations = scriptBody.getTopLevelDeclarations()
    val exportDeclarations = scriptBody.getExportDeclarations()

    return JSModuleDeclaration(
            moduleName = moduleName,
            fileName = fileName,
            exportDeclarations = exportDeclarations,
            topLevelDeclarations = topLevelDeclarations
    )
}
