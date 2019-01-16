package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.DocumentRoot
import org.jetbrains.dukat.ast.model.FunctionDeclaration
import org.jetbrains.dukat.ast.model.FunctionTypeDeclaration
import org.jetbrains.dukat.ast.model.InterfaceDeclaration
import org.jetbrains.dukat.ast.model.MemberDeclaration
import org.jetbrains.dukat.ast.model.MethodDeclaration
import org.jetbrains.dukat.ast.model.ParameterDeclaration
import org.jetbrains.dukat.ast.model.ParameterValue
import org.jetbrains.dukat.ast.model.PropertyDeclaration
import org.jetbrains.dukat.ast.model.TypeDeclaration
import org.jetbrains.dukat.ast.model.TypeParameter
import org.jetbrains.dukat.ast.model.VariableDeclaration
import org.jetbrains.dukat.ast.model.duplicate

interface Lowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration): VariableDeclaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration): FunctionDeclaration
    fun lowerClassDeclaration(declaration: ClassDeclaration): ClassDeclaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration): InterfaceDeclaration
    fun lowerPropertyDeclaration(declaration: PropertyDeclaration): PropertyDeclaration
    fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration
    fun lowerFunctionTypeDeclaration(declaration: FunctionTypeDeclaration): FunctionTypeDeclaration
    fun lowerMethodDeclaration(declaration: MethodDeclaration): MethodDeclaration
    fun lowerParameterDeclaration(declaration: ParameterDeclaration): ParameterDeclaration
    fun lowerTypeParameter(declaration: TypeParameter): TypeParameter

    fun lowerParameterValue(declaration: ParameterValue): ParameterValue {
        if (declaration is TypeDeclaration) {
            return lowerTypeDeclaration(declaration)
        } else if (declaration is FunctionTypeDeclaration) {
            return lowerFunctionTypeDeclaration(declaration)
        } else throw Exception("can not lower unknown ParameterValue subtype:  ${this} : ${declaration}")
    }

    fun lowerMemberDeclaration(declaration: MemberDeclaration): MemberDeclaration {
        if (declaration is MethodDeclaration) {
            return this.lowerMethodDeclaration(declaration)
        } else if (declaration is VariableDeclaration) {
            return this.lowerVariableDeclaration(declaration)
        } else if (declaration is PropertyDeclaration) {
            return this.lowerPropertyDeclaration(declaration)
        } else {
            throw Exception("can not lower unknown MemberDeclaration subtype ${this} : ${declaration}")
        }
    }

    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration): ClassLikeDeclaration {
        if (declaration is InterfaceDeclaration) {
            return lowerInterfaceDeclaration(declaration)
        } else if (declaration is ClassDeclaration) {
            return lowerClassDeclaration(declaration)
        } else throw Exception("can not lower unknown ClassLikeDeclaraion subtype ${this} : ${declaration}")
    }

    fun lower(documenRoot: DocumentRoot): DocumentRoot {
        val loweredDeclarations = documenRoot.declarations.map { declaration ->
            when (declaration) {
                is VariableDeclaration -> lowerVariableDeclaration(declaration)
                is FunctionDeclaration -> lowerFunctionDeclaration(declaration)
                is ClassDeclaration -> lowerClassDeclaration(declaration)
                is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration)
                else -> declaration.duplicate()
            }
        }

        return documenRoot.copy(declarations = loweredDeclarations)
    }
}
