package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun TypeValueModel.isLibReference(): Boolean {
    return fqName?.leftMost() == IdentifierEntity("<LIBROOT>")
}

private fun HeritageModel.isLibReference(): Boolean {
    return value.isLibReference()
}


private fun NameEntity.fqLib(): NameEntity {
    return IdentifierEntity("<LIBROOT>").appendLeft(this)
}

private fun String.fqLib(): NameEntity {
    return IdentifierEntity(this).fqLib()
}

private fun TypeValueModel.createStdType(name: String): TypeValueModel {
    val nameEntity = IdentifierEntity(name)
    return copy(
        value = nameEntity,
        params = emptyList(),
        fqName = nameEntity.fqLib()
    )
}

private fun TypeValueModel.resolveAsSubstitution(): TypeValueModel? {
    val isLibReference = isLibReference()

    return if (value == IdentifierEntity("NonNullable") || value == IdentifierEntity("Exclude") || value == IdentifierEntity("Required")) {
        if (isLibReference) {
            createStdType("Any")
        } else null
    } else if (value == IdentifierEntity("ReadonlyArray")) {
        if (isLibReference) {
            val fqName = "Array".fqLib()
            copy(
                value = fqName,
                fqName = fqName
            )
        } else null
    } else if (value == IdentifierEntity("TemplateStringsArray")) {
        val fqName = "Array".fqLib()
        if (isLibReference) {
            val stringFqName = "String".fqLib()
            copy(
                value = fqName,
                fqName = fqName,
                params = listOf(
                   TypeParameterModel(
                      TypeValueModel(
                           value = stringFqName,
                           fqName = stringFqName,
                           params = emptyList(),
                           metaDescription = null,
                           nullable = false
                      ),
                      emptyList()
                   )
                )
            )
        } else null
    } else null
}

@Suppress("UNCHECKED_CAST")
private fun ClassLikeModel.convertToTypeAlias(forbiddenParent: HeritageModel): TypeAliasModel {
    return TypeAliasModel(
            name = name,
            typeParameters = typeParameters,
            visibilityModifier = VisibilityModifierModel.DEFAULT,
            comment = null,
            typeReference = forbiddenParent.value.copy(params = forbiddenParent.typeParams.map {
                TypeParameterModel(it, emptyList())
            })
    )
}

private class SubstituteLowering : ModelWithOwnerTypeLowering {

    private val stdLibFinalEntities = setOf<NameEntity>(
            IdentifierEntity("Array"),
            IdentifierEntity("Error"),
            IdentifierEntity("Iterable"),
            IdentifierEntity("String")
    )

    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, moduleModel: ModuleModel): TopLevelModel {
        val declaration = ownerContext.node
        val declarationResolved = if (declaration is ClassLikeModel) {
            declaration.parentEntities.firstOrNull { parentEntity ->
                parentEntity.isLibReference() && stdLibFinalEntities.contains(parentEntity.value.value)
            }?.let { forbiddenParent ->
                declaration.convertToTypeAlias(forbiddenParent)
            }
        } else {
            null
        } ?: declaration

        return super.lowerTopLevelModel(ownerContext.copy(node = declarationResolved), moduleModel)
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val declaration = ownerContext.node
        return declaration.resolveAsSubstitution()?.let { resolved ->
            super.lowerTypeValueModel(ownerContext.copy(node = resolved))
        } ?: super.lowerTypeValueModel(ownerContext)
    }
}

private fun ModuleModel.substituteTsStdLibEntities(): ModuleModel {
    return SubstituteLowering().lowerRoot(this, NodeOwner(this, null))
}

private fun SourceFileModel.substituteTsStdLibEntities(): SourceFileModel {
    return copy(root = root.substituteTsStdLibEntities())
}

fun SourceSetModel.substituteTsStdLibEntities(): SourceSetModel {
    return copy(sources = sources.map(SourceFileModel::substituteTsStdLibEntities))
}
