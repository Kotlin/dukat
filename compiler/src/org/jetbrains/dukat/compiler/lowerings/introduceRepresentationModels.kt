package org.jetbrains.dukat.compiler.lowerings

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.CompanionObjectModel
import org.jetbrains.dukat.ast.model.model.InterfaceModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.model.SourceFileModel
import org.jetbrains.dukat.ast.model.model.SourceSetModel
import org.jetbrains.dukat.ast.model.nodes.AnnotationNode
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.DocumentRootNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.astCommon.TopLevelDeclaration


private fun MemberNode.isStatic() = when (this) {
    is MethodNode -> static
    is PropertyNode -> static
    else -> false
}

private fun ClassNode.convertToClassModel(): ClassModel {
    val staticMembers = mutableListOf<MemberNode>()
    val ownMembers = mutableListOf<MemberNode>()

    members.forEach { member ->
        if (member.isStatic()) {
            staticMembers.add(member)
        } else ownMembers.add(member)
    }

    return ClassModel(
            name = name,
            members = ownMembers,
            companionObject = CompanionObjectModel(
                    "",
                    staticMembers,
                    emptyList()
            ),
            primaryConstructor = primaryConstructor,
            typeParameters = typeParameters,
            parentEntities = parentEntities,
            annotations = annotations
    )
}

private fun InterfaceNode.convertToInterfaceModel(): InterfaceModel {
    val staticMembers = mutableListOf<MemberNode>()
    val ownMembers = mutableListOf<MemberNode>()

    members.forEach { member ->
        if (member.isStatic()) {
            staticMembers.add(member)
        } else ownMembers.add(member)
    }

    return InterfaceModel(
            name = name,
            members = ownMembers,
            companionObject = CompanionObjectModel(
                    "",
                    staticMembers,
                    emptyList()
            ),
            typeParameters = typeParameters,
            parentEntities = parentEntities,
            annotations = annotations
    )
}

fun DocumentRootNode.introduceRepresentationModels(): ModuleModel {
    val declarations = declarations.mapNotNull { declaration ->
        when (declaration) {
            is DocumentRootNode -> declaration.introduceRepresentationModels()
            is ClassNode -> declaration.convertToClassModel()
            is InterfaceNode -> declaration.convertToInterfaceModel()
            is FunctionNode -> declaration
            is EnumNode -> declaration
            is VariableNode -> declaration
            is ObjectNode -> declaration
            else -> {
                println("skipping ${declaration::class.simpleName}")
                null
            }
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
            shortName = packageName,
            qualifierName = qualifierName,
            declarations = declarationsFiltered,
            annotations = annotations,
            sumbodules = submodules
    )
}

fun SourceSetNode.introduceRepresentationModels() = SourceSetModel(
    sources = sources.map { source ->
        SourceFileModel(source.fileName, source.root.introduceRepresentationModels(), source.referencedFiles)
    }
)