package org.jetbrains.dukat.js.parser

import com.oracle.js.parser.ErrorManager
import com.oracle.js.parser.Parser
import com.oracle.js.parser.ScriptEnvironment
import com.oracle.js.parser.Source
import com.oracle.js.parser.ir.*
import org.jetbrains.dukat.js.declarations.*
import org.jetbrains.dukat.js.declarations.member.JSMethodDeclaration
import org.jetbrains.dukat.js.declarations.misc.JSParameterDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSClassDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSFunctionDeclaration
import org.jetbrains.dukat.js.declarations.toplevel.JSDeclaration
import java.io.File


private fun getBodyOfJSFile(fileName: String) : Block {
    val source = Source.sourceFor(fileName, File(fileName).readText())
    val scriptEnv = ScriptEnvironment.builder().ecmaScriptVersion(Integer.MAX_VALUE).build()

    val baseNode = Parser(scriptEnv, source, ErrorManager.ThrowErrorManager()).parse()

    return baseNode.body
}


private fun PropertyNode.toDeclaration() : JSDeclaration? {
    return when(this.value) {
        null -> null
        is FunctionNode -> {
            val function = (this.value as FunctionNode).toDeclaration()

            JSMethodDeclaration(
                    name = function.name,
                    function = function,
                    static = this.isStatic
            )
        }
        else -> null
    }
}


private fun ClassNode.toDeclaration() : JSDeclaration {
    val classDeclaration = JSClassDeclaration(
            name = ident.name,
            scopeDeclarations = mutableMapOf<String, JSDeclaration>()
    )

    for(classElement in classElements) {
        val declaration = classElement.toDeclaration()
        classDeclaration.addTopLevelDeclaration(declaration)
    }

    return classDeclaration
}

private fun IdentNode.toParameterDeclaration() : JSParameterDeclaration {
    return JSParameterDeclaration(
            name = name,
            vararg = isRestParameter
    )
}

private fun FunctionNode.toDeclaration() : JSFunctionDeclaration {
    val parameterDeclarations = mutableListOf<JSParameterDeclaration>()

    for(parameter in parameters) {
        parameterDeclarations.add(parameter.toParameterDeclaration())
    }

    return JSFunctionDeclaration(
            name = name,
            parameters = parameterDeclarations
    )
}

private fun IdentNode.toDeclaration(scope: JSScopedDeclaration) : JSDeclaration? {
    return scope.scopeDeclarations[name]
}

private fun BlockExpression.toDeclaration(scope: JSScopedDeclaration) : JSDeclaration? {
    scope.addDeclarationsFrom(block)

    return if(block.lastStatement is ExpressionStatement) {
        val lastExpression = (block.lastStatement as ExpressionStatement).expression
        lastExpression.toDeclaration(scope)
    } else {
        null
    }
}


private fun Expression.toDeclaration(scope: JSScopedDeclaration) : JSDeclaration? {
    return when(this) {
        is IdentNode -> this.toDeclaration(scope)
        is FunctionNode -> this.toDeclaration()
        is BlockExpression -> this.toDeclaration(scope)
        is ClassNode -> this.toDeclaration()
        else -> null
    }
}

private fun JSScopedDeclaration.addTopLevelDeclaration(name: String?, declaration: JSDeclaration?) {
    if(name!= null && declaration != null) {
        scopeDeclarations[name] = declaration
    }
}

private fun JSScopedDeclaration.addTopLevelDeclaration(declaration: JSDeclaration?) {
    if(declaration != null) {
        addTopLevelDeclaration(declaration.name, declaration)
    }
}

private fun JSScopedDeclaration.addTopLevelDeclaration(varNode: VarNode) {
    addTopLevelDeclaration(
            name = varNode.name.name,
            declaration = varNode.init?.toDeclaration(this)
    )
}


private fun Expression.isExportsExpression() : Boolean {
    return when(toString()) {
        "module.exports" -> true
        "exports" -> true
        else -> false
    }
}


private fun JSModuleDeclaration.modifyExportDeclaration(statement: ExpressionStatement) {
    val expression = statement.expression
    if (expression.isAssignment) {
        if (expression is BinaryNode) {
            if(expression.assignmentDest.isExportsExpression()) {
                exportDeclaration = expression.assignmentSource?.toDeclaration(this)
            }
        }
    }
}


private fun JSScopedDeclaration.addDeclarationsFrom(block: Block) {
    for(statement in block.statements) {
        if(statement is ExpressionStatement && this is JSModuleDeclaration) {
            //Check if statement defines exports (like "module.exports = ..." or similar)
            modifyExportDeclaration(statement)
        } else if(statement is VarNode) {
            //Register variable as declaration, if possible (might be value, class, function, etc.)
            addTopLevelDeclaration(statement)
        }
    }
}


fun parseJS(moduleName: String, fileName: String): JSModuleDeclaration {
    val module = JSModuleDeclaration(
            moduleName = moduleName,
            fileName = fileName,
            exportDeclaration = null,
            scopeDeclarations = mutableMapOf()
    )

    val scriptBody = getBodyOfJSFile(module.fileName)
    module.addDeclarationsFrom(scriptBody)

    return module
}

