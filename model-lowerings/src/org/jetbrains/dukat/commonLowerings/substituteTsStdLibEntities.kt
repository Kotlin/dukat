package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.SimpleCommentEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceFileModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.modifiers.VisibilityModifierModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.stdlib.isTsStdlibPrefixed

private fun TypeValueModel.isLibReference(): Boolean {
    return fqName?.isTsStdlibPrefixed() == true
}

private fun HeritageModel.isLibReference(): Boolean {
    return value.isLibReference()
}


private fun NameEntity.fqLib(): NameEntity {
    return TSLIBROOT.appendLeft(this)
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

private enum class SubstitutedEntities(val value: IdentifierEntity) {
    NON_NULLABLE(IdentifierEntity("NonNullable")),
    EXCLUDE(IdentifierEntity("Exclude")),
    REQUIRED(IdentifierEntity("Required")),
    READONLY_ARRAY(IdentifierEntity("ReadonlyArray")),
    TEMPLATE_STRINGS_ARRAY(IdentifierEntity("TemplateStringsArray"))
}

private fun TypeValueModel.resolveAsSubstitution(): TypeValueModel? {
    val isLibReference = isLibReference()

    return if (value == SubstitutedEntities.NON_NULLABLE.value || value == SubstitutedEntities.EXCLUDE.value || value == SubstitutedEntities.REQUIRED.value) {
        if (isLibReference) {
            createStdType("Any")
        } else null
    } else if (value == SubstitutedEntities.READONLY_ARRAY.value) {
        if (isLibReference) {
            val fqName = "Array".fqLib()
            copy(
                    value = fqName,
                    fqName = fqName
            )
        } else null
    } else if (value == SubstitutedEntities.TEMPLATE_STRINGS_ARRAY.value) {
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
            typeParameters = typeParameters.map { it.copy(constraints = emptyList()) },
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
            IdentifierEntity("Function"),
            IdentifierEntity("ReadonlyArray"),
            IdentifierEntity("Error"),
            IdentifierEntity("SyntaxError"),
            IdentifierEntity("Iterable"),
            IdentifierEntity("Iterator"),
            IdentifierEntity("String")
    )

    private val substituteInlines = mapOf(
            Pair(SubstitutedEntities.READONLY_ARRAY.value, "Array")
    )

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val declaration = ownerContext.node
        val declarationResolved = declaration.extend?.let { extendedInterface ->
            var commentResolved = declaration.comment

            val extendResolved = substituteInlines.get(extendedInterface.name)?.let { substitutedName ->
                val comment = declaration.comment
                commentResolved = when (comment) {
                    is SimpleCommentEntity -> SimpleCommentEntity("${comment.text}; replaced ${SubstitutedEntities.READONLY_ARRAY.value.value} => ${substitutedName} ")
                    else -> comment
                }

                extendedInterface.copy(name = IdentifierEntity(substitutedName))
            } ?: extendedInterface

            declaration.copy(
                    extend = extendResolved,
                    comment = commentResolved
            )
        } ?: declaration
        return super.lowerFunctionModel(ownerContext.copy(node = declarationResolved), parentModule)
    }

    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, parentModule: ModuleModel): TopLevelModel? {
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

        return super.lowerTopLevelModel(ownerContext.copy(node = declarationResolved), parentModule)
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val declaration = ownerContext.node
        return declaration.resolveAsSubstitution()?.let { resolved ->
            super.lowerTypeValueModel(ownerContext.copy(node = resolved))
        } ?: super.lowerTypeValueModel(ownerContext)
    }
}

class SubstituteTsStdLibEntities() : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return SubstituteLowering().lowerRoot(module, NodeOwner(module, null))
    }
}
