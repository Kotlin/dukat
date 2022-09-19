package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.*
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap
import org.jetbrains.dukat.stdlib.isTsStdlibPrefixed

private class ReplaceDynamicsTypeLowering : ModelWithOwnerTypeLowering {
    private fun NameEntity.renameDynamic(): NameEntity {
        if (!isDynamic) return this
        return when (this) {
            is QualifierEntity -> copy(right = IdentifierEntity("Dynamic"))
            is IdentifierEntity -> IdentifierEntity(value = "Dynamic")
        }
    }

    private val NameEntity.isDynamic: Boolean
        get() = when (this) {
            is QualifierEntity -> isTsStdlibPrefixed() && right.value == "dynamic"
            is IdentifierEntity -> this.value == "dynamic"
        }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val type = ownerContext.node
        if (!(type.fqName ?: type.value).isDynamic) return super.lowerTypeValueModel(ownerContext)

        return super.lowerTypeValueModel(
                ownerContext.copy(
                        node = ownerContext.node.copy(
                                value = type.value.renameDynamic(),
                                fqName = type.fqName?.renameDynamic(),
                                nullable = true
                        )
                )
        )
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        return super.lowerParameterModel(
                ownerContext.copy(
                        node = declaration.copy(
                                initializer = declaration.initializer?.let(::lower)
                        )
                )
        )
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext, moduleModel),
                submodules = moduleModel.submodules.map { submodule -> lowerRoot(submodule, ownerContext.wrap(submodule)) }
        )
    }
}

class ReplaceDynamics : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel =
            ReplaceDynamicsTypeLowering().lowerRoot(module, NodeOwner(module, null))
}