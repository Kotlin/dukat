package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.StatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

interface DeclarationStatementLowering {

    fun lowerExpression(expression: ExpressionDeclaration): ExpressionDeclaration
    fun lowerStatement(statement: StatementDeclaration): List<StatementDeclaration>

    fun lowerBlockStatement(block: BlockDeclaration): BlockDeclaration {
        return BlockDeclaration(block.statements.flatMap { lowerStatement(it) })
    }

    fun lowerTopLevelVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration {
        return declaration.copy(
            initializer = declaration.initializer?.let { lowerExpression(it) }
        )
    }

    fun lowerConstructorDeclaration(declaration: ConstructorDeclaration): ConstructorDeclaration {
        return declaration.copy(body = declaration.body?.let { lowerBlockStatement(it) })
    }

    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration {
        return declaration.copy(body = declaration.body?.let { lowerBlockStatement(it) })
    }

    fun lowerPropertyDeclaration(declaration: PropertyDeclaration): PropertyDeclaration {
        return declaration.copy(initializer = declaration.initializer?.let { lowerExpression(it) })
    }

    fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration {
        return when (declaration) {
            is ConstructorDeclaration -> lowerConstructorDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is PropertyDeclaration -> lowerPropertyDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        val newMembers = declaration.members.map { lowerMemberDeclaration(it) }
        return when (declaration) {
            is InterfaceDeclaration -> declaration.copy(members = newMembers)
            is ClassDeclaration -> declaration.copy(members = newMembers)
            is GeneratedInterfaceDeclaration -> declaration.copy(members = newMembers)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration): TopLevelDeclaration {
        return when (declaration) {
            is VariableDeclaration -> lowerTopLevelVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration)
            is ModuleDeclaration -> lowerDocumentRoot(declaration)
            else -> declaration
        }
    }

    fun lowerTopLevelDeclarations(declarations: List<TopLevelDeclaration>): List<TopLevelDeclaration> {
        return declarations.map { declaration ->
            lowerTopLevelDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documentRoot: ModuleDeclaration): ModuleDeclaration {
        return documentRoot.copy(declarations = lowerTopLevelDeclarations(documentRoot.declarations))
    }
}