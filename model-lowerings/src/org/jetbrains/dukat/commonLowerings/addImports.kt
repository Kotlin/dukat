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
import org.jetbrains.dukat.model.commonLowerings.escape
import org.jetbrains.dukat.ownerContext.NodeOwner

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
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext, moduleModel)
        )
    }

    fun process(moduleModel: ModuleModel) {
        lowerRoot(moduleModel, NodeOwner(moduleModel, null))
    }
}

private fun NameEntity.normalize(): NameEntity? {
    // TODO: we need this only because some packages generated from type aliases has no <ROOT> prefix - I need to investigate why
    return if (leftMost() == IdentifierEntity("<ROOT>")) {
        this.shiftLeft()
    } else {
        this
    }
}

private fun ModuleModel.addImports(): ModuleModel {
    val resolveImports = linkedSetOf<NameEntity>()
    val moduleWithFqName = TypeVisitor { typeModel ->
        typeModel.fqName?.shiftRight()?.let { moduleName ->
            if (moduleName.normalize() != name.normalize()) {
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