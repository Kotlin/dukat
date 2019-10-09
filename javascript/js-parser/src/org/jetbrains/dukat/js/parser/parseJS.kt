package org.jetbrains.dukat.js.parser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.js.declarations.*
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSClassDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration
import java.io.File



class JSModuleParser(moduleName: String, fileName: String) {

    private val module: JSModuleDeclaration = JSModuleDeclaration(
            moduleName = moduleName,
            fileName = fileName,
            exportDeclarations = mutableSetOf(),
            topLevelDeclarations = mutableMapOf()
    )

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

    private fun ClassNode.toDeclaration() : JSDeclaration {
        return JSClassDeclaration(
                name = ident.name,
                methods = mutableSetOf() //TODO fill
        )
    }

    private fun FunctionNode.toDeclaration() : JSDeclaration {
        val parameterDeclarations = mutableListOf<JSParameterDeclaration>()

        for(parameter in parameters) {
            parameterDeclarations.add(parameter.toParameterDeclaration())
        }

        return JSFunctionDeclaration(
                name = name,
                parameters = parameterDeclarations
        )
    }

    private fun IdentNode.toDeclaration() : JSDeclaration? {
        return module.topLevelDeclarations[name]
    }

    private fun BlockExpression.toDeclaration() : JSDeclaration? {
        addDeclarations(block)

        //TODO generate return type
        return null
    }


    private fun Expression.toDeclaration() : JSDeclaration? {
        return when(this) {
            is IdentNode -> this.toDeclaration()
            is FunctionNode -> this.toDeclaration()
            is BlockExpression -> this.toDeclaration()
            is ClassNode -> this.toDeclaration()
            else -> null
        }
    }

    private fun VarNode.toDeclaration() : JSDeclaration? {
        return init?.toDeclaration()
    }


    private fun addTopLevelDeclaration(name: String?, declaration: JSDeclaration?) {
        if(name!= null && declaration != null) {
            module.topLevelDeclarations[name] = declaration
        }
    }

    private fun addTopLevelDeclaration(varNode: VarNode) {
        addTopLevelDeclaration(
                name = varNode.name.name,
                declaration = varNode.toDeclaration()
        )
    }

    private fun addExportDeclaration(statement: ExpressionStatement) {
        val expression = statement.expression
        if (expression.isAssignment) {
            if (expression is BinaryNode) {
                if(expression.assignmentDest.isExportsExpression()) {
                    val exportDeclaration = expression.assignmentSource?.toDeclaration()

                    if(exportDeclaration != null) {
                        module.exportDeclarations.add(exportDeclaration)
                    }
                }
            }
        }
    }


    private fun addDeclarations(block: Block) {
        for(statement in block.statements) {
            if(statement is ExpressionStatement) {
                //Check if statement defines exports (like "module.exports = ..." or similar)
                addExportDeclaration(statement)
            } else if(statement is VarNode) {
                //Register variable as declaration, if possible (might be value, class, function, etc.)
                addTopLevelDeclaration(statement)
            }
        }
    }


    fun parse(): JSModuleDeclaration {
        val scriptBody = getBodyOfJSFile(module.fileName)

        addDeclarations(scriptBody)

        return module
    }
}

