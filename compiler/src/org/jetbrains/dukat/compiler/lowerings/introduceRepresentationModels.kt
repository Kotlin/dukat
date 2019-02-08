package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.astCommon.MemberDeclaration
import org.jetbrains.dukat.astCommon.TopLevelDeclaration


private fun MemberDeclaration.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}

private fun ClassNode.convertToClassModel() : ClassModel {
    val staticMembers = mutableListOf<MemberDeclaration>()
    val ownMembers = mutableListOf<MemberDeclaration>()

    members.forEach { member ->
        if (member.isStatic()) {
            staticMembers.add(member)
        } else ownMembers.add(member)
    }

    return ClassModel(
        name = name,
        members = ownMembers,
        companionObject = ObjectNode(
            "",
                staticMembers,
                mutableListOf()
        ),
        primaryConstructor = primaryConstructor,
        typeParameters = typeParameters,
        parentEntities = parentEntities,
        annotations = annotations
    )
}

fun DocumentRootNode.introduceRepresentationModels(): ModuleModel {
    val declarations = declarations.map {declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceRepresentationModels()
            is ClassNode -> declaration.convertToClassModel()
            else -> declaration
        }
    }


    val declarationsFiltered = mutableListOf<TopLevelDeclaration>()
    val submodules = mutableListOf<ModuleModel>()
    declarations.forEach { declaration ->
        if (declaration is ModuleModel) submodules.add(declaration) else declarationsFiltered.add(declaration)
    }

    val annotations = mutableListOf<AnnotationNode>()

    if (showQualifierAnnotation) {
        val qualifier = if (isQualifier) "JsQualifier" else "JsModule"
        annotations.add(AnnotationNode("file:${qualifier}", listOf(qualifierName)))
    }

    return ModuleModel(
        packageName = fullPackageName,
        declarations = declarationsFiltered,
        annotations = annotations,
        sumbodules = submodules
    )
}