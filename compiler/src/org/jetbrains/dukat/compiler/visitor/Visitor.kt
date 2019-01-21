package org.jetbrains.dukat.compiler.visitor

import org.jetbrains.dukat.ast.model.ClassDeclaration
import org.jetbrains.dukat.ast.model.ClassLikeDeclaration
import org.jetbrains.dukat.ast.model.Declaration
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
import org.jetbrains.dukat.ast.model.extended.ObjectLiteral

interface Visitor {
    fun visitVariableDeclaration(declaration: VariableDeclaration) {
        visitParameterValue(declaration.type)
    }

    fun visitFunctionDeclaration(declaration: FunctionDeclaration) {
        declaration.parameters.forEach { parameter -> visitParameterValue(parameter.type) }
        declaration.typeParameters.forEach { typeParameter -> typeParameter.constraints.forEach { constraint -> visitParameterValue(constraint) }}
        visitParameterValue(declaration.type)
    }

    fun visitClassDeclaration(declaration: ClassDeclaration) {
        declaration.members.forEach { member -> visitMemberDeclaration(member) }
        declaration.primaryConstructor?.let { visitMethodDeclaration(it) }
        declaration.parentEntities.forEach { parentEntity -> visitClassLikeDeclaration(parentEntity) }
        declaration.typeParameters.forEach {
            typeParameter -> visitTypeParameter(typeParameter)
        }
    }

    fun visitInterfaceDeclaration(declaration: InterfaceDeclaration) {
        declaration.members.forEach { member -> visitMemberDeclaration(member) }
        declaration.parentEntities.forEach { parentEntity -> visitInterfaceDeclaration(parentEntity) }
        declaration.typeParameters.forEach {
            typeParameter -> visitTypeParameter(typeParameter)
        }
    }

    fun visitPropertyDeclaration(declaration: PropertyDeclaration) {
        visitParameterValue(declaration.type)
        declaration.typeParameters.forEach {typeParameter -> visitTypeParameter(typeParameter)}
    }

    fun visitTypeDeclaration(declaration: TypeDeclaration) {
        declaration.params.forEach { param -> visitParameterValue(param) }
    }

    fun visitFunctionTypeDeclaration(declaration: FunctionTypeDeclaration) {
        declaration.parameters.forEach { param -> visitParameterDeclaration(param) }
        visitParameterValue(declaration.type)
    }

    fun visitMethodDeclaration(declaration: MethodDeclaration) {
        declaration.parameters.forEach { parameter -> visitParameterValue(parameter.type) }
        declaration.typeParameters.forEach { typeParameter ->
            typeParameter.constraints.forEach { constraint -> visitParameterValue(constraint) }
        }
        visitParameterValue(declaration.type)
    }

    fun visitParameterDeclaration(declaration: ParameterDeclaration) {
        visitParameterValue(declaration.type)
    }

    fun visitTypeParameter(declaration: TypeParameter) {
        declaration.constraints.forEach {constraint -> visitParameterValue(constraint)}
    }

    fun visitObjectLiteral(declaration: ObjectLiteral) {
        throw Exception("Object literals supposed not to be in AST tree passed to translation")
    }

    fun visitParameterValue(declaration: ParameterValue) {
        if (declaration is TypeDeclaration) {
            return visitTypeDeclaration(declaration)
        } else if (declaration is FunctionTypeDeclaration) {
            return visitFunctionTypeDeclaration(declaration)
        } else if (declaration is ObjectLiteral) {
            return visitObjectLiteral(declaration)
        } else throw Exception("can not visit unknown ParameterValue subtype:  ${this} : ${declaration}")
    }

    fun visitMemberDeclaration(declaration: MemberDeclaration) {
        if (declaration is MethodDeclaration) {
            return visitMethodDeclaration(declaration)
        } else if (declaration is PropertyDeclaration) {
            return visitPropertyDeclaration(declaration)
        } else {
            throw Exception("can not visit unknown MemberDeclaration subtype ${this} : ${declaration}")
        }
    }

    fun visitClassLikeDeclaration(declaration: ClassLikeDeclaration) {
        if (declaration is InterfaceDeclaration) {
            return visitInterfaceDeclaration(declaration)
        } else if (declaration is ClassDeclaration) {
            return visitClassDeclaration(declaration)
        } else throw Exception("can not visit unknown ClassLikeDeclaraion subtype ${this} : ${declaration}")
    }

    fun visitDeclaration(declaration: Declaration) {
        return when (declaration) {
            is VariableDeclaration -> visitVariableDeclaration(declaration)
            is FunctionDeclaration -> visitFunctionDeclaration(declaration)
            is ClassDeclaration -> visitClassDeclaration(declaration)
            is InterfaceDeclaration -> visitInterfaceDeclaration(declaration)
            else -> Unit
        }
    }

    fun visitDocumentRoot(documenRoot: DocumentRoot) {
        documenRoot.declarations.forEach() { declaration ->
            visitDeclaration(declaration)
        }
    }
}
