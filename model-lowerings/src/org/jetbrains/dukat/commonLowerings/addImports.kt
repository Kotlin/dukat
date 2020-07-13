package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.model.commonLowerings.ModelLowering
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.stdlib.isStdLibEntity

private fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> when (value) {
        "<ROOT>" -> ""
        else -> value
    }
    is QualifierEntity -> "${left.translate()}.${right.translate()}"
}

private fun NameEntity.isTopLevelImport(): Boolean {
    if (this is QualifierEntity) {
        return leftMost() == IdentifierEntity("<ROOT>")
    }

    return false
}

private class NameVisitor(private val name: NameEntity, private val importContext: MutableMap<NameEntity, NameEntity>) : ModelWithOwnerTypeLowering {
    val resolvedImports = linkedSetOf<ImportModel>()

    private fun getNewName(oldName: NameEntity): NameEntity? {
        val moduleName = oldName.shiftRight()
        val shortName = oldName.rightMost()

        return if (!importContext.containsKey(shortName)) {
            if (moduleName != null) {
                if (moduleName.normalize() != name.normalize()) {
                    if (!isStdLibEntity(oldName)) {
                        importContext[shortName] = moduleName
                        resolvedImports.add(ImportModel(oldName))
                    }
                }
            }
            shortName
        } else if (importContext[shortName] == moduleName) {
            shortName
        } else {
            val conflictingImport = resolvedImports.firstOrNull { (it.name.rightMost() == shortName) && (it.name != oldName) }
            if ((conflictingImport != null) && (oldName.isTopLevelImport())) {
                val alias = IdentifierEntity(oldName.translate().replace(".", "_"))
                resolvedImports.add(ImportModel(oldName, alias))
                alias
            } else null
        }
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node

        val ownerContextResolved = node.fqName?.let { fqName ->
            getNewName(fqName)?.let { ownerContext.copy(node = node.copy(value = it)) }
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
    // TODO: we need this only because some packages generated from type aliases have no <ROOT> prefix - I need to investigate why
    return if (leftMost() == IdentifierEntity("<ROOT>")) {
        this.shiftLeft()
    } else {
        this
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun ModuleModel.addImports(): ModuleModel {

    val nameVisitor = NameVisitor(name, buildMap<NameEntity, NameEntity> {
        declarations.forEach {
            this[it.name] = name
        }
    }.toMutableMap())

    val moduleWithFqNames = nameVisitor.lowerRoot(this, NodeOwner(this, null))

    return moduleWithFqNames.copy(
            imports = (nameVisitor.resolvedImports + moduleWithFqNames.imports).toMutableList(),
            submodules = moduleWithFqNames.submodules.map { submodule -> submodule.addImports() }
    )
}

class AddImports : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return module.addImports()
    }
}