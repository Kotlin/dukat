package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner


private fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> value
    is QualifierEntity -> {
        "${left.translate()}.${right.translate()}"
    }
}

private class TypeVisitor(private val visit: (TypeValueModel) -> Unit) : ModelWithOwnerTypeLowering {
    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node
        visit(node)

        val ownerContextResolved = node.fqName?.let { fqName ->
            ownerContext.copy(node = node.copy(value = fqName.rightMost()))
        } ?: ownerContext

        return super.lowerTypeValueModel(ownerContextResolved)
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext)
        )
    }

    fun process(moduleModel: ModuleModel) {
        lowerRoot(moduleModel, NodeOwner(moduleModel, null))
    }
}

private fun ModuleModel.addImports(): ModuleModel {
    val resolveImports = linkedSetOf<NameEntity>()
    val moduleWithFqName = TypeVisitor { typeModel ->
        typeModel.fqName?.shiftRight()?.let { fqNameNormalized ->
            if (fqNameNormalized != name) {
                resolveImports.add(typeModel.fqName!!)
            }
        }
    }.lowerRoot(this, NodeOwner(this, null))

    return moduleWithFqName.copy(
            imports = (resolveImports + moduleWithFqName.imports).toMutableList(),
            submodules = moduleWithFqName.submodules.map { submodule -> submodule.addImports() }
    )
}

fun SourceSetModel.addImports(): SourceSetModel = transform { it.addImports() }