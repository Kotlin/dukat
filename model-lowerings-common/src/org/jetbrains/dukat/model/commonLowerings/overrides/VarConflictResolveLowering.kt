package org.jetbrains.dukat.model.commonLowerings.overrides

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.model.commonLowerings.MemberData
import org.jetbrains.dukat.model.commonLowerings.ModelContext
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.model.commonLowerings.OverrideTypeChecker
import org.jetbrains.dukat.model.commonLowerings.allParentMembers
import org.jetbrains.dukat.model.commonLowerings.buildInheritanceGraph
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun PropertyModel.toVal(): PropertyModel {
    return copy(
        immutable = true,
        setter = false
    )
}

private class VarOverrideResolver(
    private val modelContext: ModelContext,
    private val inheritanceContext: InheritanceContext
) : ModelWithOwnerTypeLowering {

    val propertiesToChangeToVal: MutableList<MemberData> = mutableListOf()

    private fun findConflictingProperties(
        parentMembers: Map<NameEntity?, List<MemberData>>,
        typeChecker: OverrideTypeChecker
    ): List<List<MemberData>> {
        return parentMembers.keys.mapNotNull { name ->
            val properties = parentMembers[name].orEmpty().filter { it.memberModel is PropertyModel }
            if (properties.isEmpty() || properties.all {
                    with(typeChecker) {
                        (it.memberModel as PropertyModel).type.isEquivalent((properties[0].memberModel as PropertyModel).type)
                    }
                }) {
                null
            } else {
                properties
            }
        }
    }

    private fun findSuperType(types: List<TypeModel>, typeChecker: OverrideTypeChecker): TypeModel? {
        var superType: TypeModel? = types[0]
        types.forEach { type ->
            if (with(typeChecker) { superType?.isOverridingReturnType(type) } == true) {
                superType = type
            } else if (superType != null && with(typeChecker) { !type.isOverridingReturnType(superType!!) }) {
                superType = null
            }
        }
        return superType
    }

    private fun createPropertyToAdd(properties: List<MemberData>, classLike: ClassLikeModel): PropertyModel? {
        val name = (properties[0].memberModel as PropertyModel).name
        return if (classLike.members.filterIsInstance<PropertyModel>().any { it.name == name }) {
            null
        } else {
            val superType = findSuperType(
                properties.map { (it.memberModel as PropertyModel).type },
                OverrideTypeChecker(modelContext, inheritanceContext, classLike, null)
            )
            superType?.let { type ->
                (properties[0].memberModel as PropertyModel).copy(
                    type = type,
                    override = properties[0].fqName
                ).toVal()
            }
        }
    }

    private fun generateNewMembers(allConflictingProperties: List<List<MemberData>>, classLike: ClassLikeModel): List<MemberModel> {

        val propertiesToAdd = allConflictingProperties.mapNotNull { createPropertyToAdd(it, classLike) }

        return classLike.members.map { member ->
            if (member is PropertyModel && allConflictingProperties.any { properties ->
                    (properties[0].memberModel as PropertyModel).name == member.name
                }) {
                member.toVal()
            } else {
                member
            }
        } + propertiesToAdd
    }

    override fun lowerClassLikeModel(
        ownerContext: NodeOwner<ClassLikeModel>,
        parentModule: ModuleModel
    ): ClassLikeModel? {
        val classLike = ownerContext.node

        val parentMembers = classLike.allParentMembers(modelContext)

        val allConflictingProperties = findConflictingProperties(
            parentMembers,
            OverrideTypeChecker(modelContext, inheritanceContext, classLike, null)
        )

        allConflictingProperties.forEach { propertiesToChangeToVal += it }

        val newMembers = generateNewMembers(allConflictingProperties, classLike)

        return when (classLike) {
            is InterfaceModel -> classLike.copy(members = newMembers)
            is ClassModel -> classLike.copy(members = newMembers)
            else -> classLike
        }
    }
}

private class VarToValResolver(private val propertiesToChangeToVal: List<MemberData>) : ModelWithOwnerTypeLowering {

    private var currentFqName: NameEntity = IdentifierEntity("")

    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val property = ownerContext.node

        return if (propertiesToChangeToVal.any {
                (it.memberModel is PropertyModel) && (it.memberModel.name == property.name) && (it.fqName == currentFqName)
            }) {
            property.toVal()
        } else {
            property
        }
    }

    override fun lowerClassLikeModel(
        ownerContext: NodeOwner<ClassLikeModel>,
        parentModule: ModuleModel
    ): ClassLikeModel? {
        currentFqName = currentFqName.appendLeft(ownerContext.node.name)
        val newClassLike = super.lowerClassLikeModel(ownerContext, parentModule)
        currentFqName = currentFqName.shiftRight()!!
        return newClassLike
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        currentFqName = moduleModel.name
        return super.lowerRoot(moduleModel, ownerContext)
    }
}

class VarConflictResolveLowering : ModelLowering {

    override fun lower(source: SourceSetModel): SourceSetModel {
        val modelContext = ModelContext(source)
        val inheritanceContext = InheritanceContext(modelContext.buildInheritanceGraph())
        val varOverrideResolver = VarOverrideResolver(modelContext, inheritanceContext)
        val newSourceSet = source.copy(
            sources = source.sources.map {
                it.copy(root = varOverrideResolver.lowerRoot(it.root, NodeOwner(it.root, null)))
            }
        )
        val varToValResolver = VarToValResolver(varOverrideResolver.propertiesToChangeToVal)
        return newSourceSet.copy(
            sources = newSourceSet.sources.map {
                it.copy(root = varToValResolver.lowerRoot(it.root, NodeOwner(it.root, null)))
            }
        )
    }

    override fun lower(module: ModuleModel): ModuleModel {
        return module
    }
}