package org.jetbrains.dukat.js.parser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.js.declarations.*
import org.jetbrains.dukat.js.declarations.export.JSExportDeclaration
import org.jetbrains.dukat.js.declarations.export.JSInlineExportDeclaration
import org.jetbrains.dukat.js.declarations.export.JSReferenceExportDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSClassDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSReferenceDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSTopLevelDeclaration
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


private fun IdentNode.toParameterDeclaration() : JSParameterDeclaration {
    return JSParameterDeclaration(
            name = name,
            vararg = isRestParameter
    )
}

private fun ClassNode.toTopLevelDeclaration() : JSTopLevelDeclaration {
    return JSClassDeclaration(
            name = ident.name,
            methods = mutableSetOf() //TODO fill
    )
}

private fun FunctionNode.toTopLevelDeclaration() : JSTopLevelDeclaration {
    val parameterDeclarations = mutableListOf<JSParameterDeclaration>()

    for(parameter in parameters) {
        parameterDeclarations.add(parameter.toParameterDeclaration())
    }

    return JSFunctionDeclaration(
            name = name,
            parameters = parameterDeclarations
    )
}



private fun FunctionNode.toExportDeclaration() : JSExportDeclaration {
    return JSInlineExportDeclaration(
            name = name,
            declaration = this.toTopLevelDeclaration()
    )
}

private fun IdentNode.toExportDeclaration() : JSExportDeclaration {
    return JSReferenceExportDeclaration(
            name = name
    )
}

private fun Expression.toExportDeclaration() : JSExportDeclaration? {
    return when(this) {
        is FunctionNode -> this.toExportDeclaration()
        is IdentNode -> this.toExportDeclaration()
        else -> null
    }
}



class JSModuleParser(moduleName: String, fileName: String) {

    private val module: JSModuleDeclaration = JSModuleDeclaration(
            moduleName = moduleName,
            fileName = fileName,
            exportDeclarations = mutableSetOf(),
            topLevelDeclarations = mutableMapOf()
    )


    private fun BlockExpression.toTopLevelDeclaration() : JSTopLevelDeclaration? {
        addTopLevelDeclarations(block)
        /*
        if(block.lastStatement is ExpressionStatement) {
            //TODO generate return type
            (block.lastStatement as ExpressionStatement).expression.toSOMETHING()
        }
        */

        return null
    }

    private fun VarNode.toTopLevelDeclaration() : JSTopLevelDeclaration? {
        return when(val expression = init) {
            null -> null
            is FunctionNode -> expression.toTopLevelDeclaration()
            is BlockExpression -> expression.toTopLevelDeclaration()
            is ClassNode -> expression.toTopLevelDeclaration()
            else -> null
        }
    }


    private fun addTopLevelDeclaration(name: String?, declaration: JSTopLevelDeclaration?) {
        if(name!= null && declaration != null) {
            module.topLevelDeclarations[name] = declaration
        }
    }

    private fun addTopLevelDeclaration(varNode: VarNode) {
        addTopLevelDeclaration(
                name = varNode.name.name,
                declaration = varNode.toTopLevelDeclaration()
        )
    }

    private fun addTopLevelDeclarations(block: Block) {
        for (statement in block.statements) {
            if(statement is VarNode) {
                addTopLevelDeclaration(statement)
            }
        }
    }


    private fun addExportDeclaration(expression: Expression) {
        val exportDeclaration = expression.toExportDeclaration()

        if(exportDeclaration != null) {
            module.exportDeclarations.add(exportDeclaration)
        }
    }

    private fun addExportDeclarations(block: Block) {
        for (statement in block.statements) {
            if (statement is ExpressionStatement) {
                val expression = statement.expression

                if (expression.isAssignment) {
                    if (expression is BinaryNode) {
                        if(expression.assignmentDest.isExportsExpression()) {
                            addExportDeclaration(expression.assignmentSource)
                        }
                    }
                }
            }
        }
    }


    fun parse(): JSModuleDeclaration {
        val scriptBody = getBodyOfJSFile(module.fileName)

        addTopLevelDeclarations(scriptBody)
        addExportDeclarations(scriptBody)

        return module
    }
}

