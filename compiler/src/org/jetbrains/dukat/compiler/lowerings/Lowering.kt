package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.declaration.ClassDeclaration
import org.jetbrains.dukat.ast.model.declaration.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.declaration.Declaration
import org.jetbrains.dukat.ast.model.declaration.DocumentRootDeclaration
import org.jetbrains.dukat.ast.model.declaration.FunctionDeclaration
import org.jetbrains.dukat.ast.model.declaration.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.declaration.MemberDeclaration
import org.jetbrains.dukat.ast.model.declaration.ParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.TypeParameterDeclaration
import org.jetbrains.dukat.ast.model.declaration.VariableDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.ParameterValueDeclaration
import org.jetbrains.dukat.ast.model.declaration.types.TypeDeclaration
import org.jetbrains.dukat.ast.model.duplicate

interface Lowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration
    fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameterDeclaration): TypeParameterDeclaration
    fun lowerObjectLiteral(declaration: ObjectLiteralDeclaration): ObjectLiteralDeclaration
    fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration

    fun lowerParameterValue(declaration: ParameterValueDeclaration): ParameterValueDeclaration {
        return when (declaration) {
            is TypeDeclaration -> lowerTypeDeclaration(declaration)
            is FunctionTypeDeclaration -> lowerFunctionTypeDeclaration(declaration)
            is ObjectLiteralDeclaration -> lowerObjectLiteral(declaration)
            else -> throw Exception("can not lowerParameterValue unknown ParameterValueDeclaration subtype:  ${this} : ${declaration}")
        }
    }


    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is ClassDeclaration -> lowerClassDeclaration(declaration)
            else -> declaration
        }
    }

    fun lowerDeclaration(declaration: Declaration) : Declaration {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
            is ClassDeclaration -> lowerClassDeclaration(declaration)
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
            is DocumentRootDeclaration -> lowerDocumentRoot(declaration)
            else -> declaration.duplicate()
        }
    }

    fun lowerDeclarations(declarations: List<Declaration>) : List<Declaration> {
        return declarations.map { declaration ->
            lowerDeclaration(declaration)
        }
    }

    fun lowerDocumentRoot(documenRoot: DocumentRootDeclaration): DocumentRootDeclaration {
        return documenRoot.copy(declarations = lowerDeclarations(documenRoot.declarations))
    }
}
