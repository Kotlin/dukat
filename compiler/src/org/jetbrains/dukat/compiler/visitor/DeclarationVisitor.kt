package org.jetbrains.dukat.compiler.visitor

import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.DocumentRootDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

interface DeclarationVisitor {


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
        //declaration.primaryConstructor?.let { visitFunctionDeclaration(it) }
//        declaration.parentEntities.forEach { parentEntity -> visitClassLikeDeclaration(parentEntity) }
        declaration.typeParameters.forEach {
            typeParameter -> visitTypeParameter(typeParameter)
        }
    }

    fun visitInterfaceDeclaration(declaration: InterfaceDeclaration) {
        declaration.members.forEach { member -> visitMemberDeclaration(member) }
        //declaration.parentEntities.forEach { parentEntity -> visitInterfaceDeclaration(parentEntity) }
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

    fun visitParameterDeclaration(declaration: ParameterDeclaration) {
        visitParameterValue(declaration.type)
    }

    fun visitTypeParameter(declaration: TypeParameterDeclaration) {
        declaration.constraints.forEach {constraint -> visitParameterValue(constraint)}
    }

    fun visitObjectLiteral(declaration: ObjectLiteralDeclaration) {
        throw Exception("Object literals supposed not to be in AST tree passed to translation")
    }

    fun visitParameterValue(declaration: ParameterValueDeclaration) {
        if (declaration is TypeDeclaration) {
            return visitTypeDeclaration(declaration)
        } else if (declaration is FunctionTypeDeclaration) {
            return visitFunctionTypeDeclaration(declaration)
        } else if (declaration is ObjectLiteralDeclaration) {
            return visitObjectLiteral(declaration)
        } else throw Exception("can not visit unknown ParameterValueDeclaration subtype:  ${this} : ${declaration}")
    }

    fun visitMemberDeclaration(declaration: MemberDeclaration) {
//        if (declaration is MethodSignatureDeclaration) {
//            return visitMethodDeclaration(declaration)
//        } else if (declaration is PropertyDeclaration) {
        if (declaration is PropertyDeclaration) {
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

    fun visitDocumentRoot(documenRoot: DocumentRootDeclaration) {
        documenRoot.declarations.forEach() { declaration ->
            visitDeclaration(declaration)
        }
    }
}
