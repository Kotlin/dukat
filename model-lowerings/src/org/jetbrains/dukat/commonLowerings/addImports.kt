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
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun NameEntity.translate(): String = when (this) {
    is IdentifierEntity -> when(value) {
        "<ROOT>" -> ""
        "<LIBROOT>" -> ""
        else -> value
    }
    is QualifierEntity -> "${left.translate()}.${right.translate()}"
}

private fun NameEntity.isTopLevelImport(): Boolean {
    if (this is QualifierEntity) {
        return left == IdentifierEntity("<ROOT>")
    }

    return false
}

private class TypeVisitor(private val name: NameEntity, private val importContext: MutableMap<NameEntity, NameEntity>) : ModelWithOwnerTypeLowering {
    val resolvedImports = linkedSetOf<ImportModel>()

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node

        val ownerContextResolved = node.fqName?.let { fqName ->
            val moduleName = fqName.shiftRight()
            val shortName = fqName.rightMost()

            if (!importContext.containsKey(shortName)) {
                if (moduleName != null) {
                    if (moduleName.normalize() != name.normalize()) {
                        importContext[shortName] = moduleName
                        resolvedImports.add(ImportModel(fqName))
                    }
                }
                ownerContext.copy(node = node.copy(value = shortName))
            } else if (importContext[shortName] == moduleName) {
                ownerContext.copy(node = node.copy(value = shortName))
            } else {
                val conflictingImport = resolvedImports.firstOrNull { (it.name.rightMost() == shortName) && (it.name != fqName) }
                if ((conflictingImport != null) && (fqName.isTopLevelImport())) {
                    val alias = IdentifierEntity(fqName.translate().replace(".", "_"))
                    resolvedImports.add(ImportModel(fqName, alias.value))
                    ownerContext.copy(node = node.copy(value = alias))
                } else null
            }
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

private fun ModuleModel.addImports(): ModuleModel {
    val importContext = mutableMapOf<NameEntity, NameEntity>()
    declarations.map { importContext[it.name] = name }

    val typeVisitor = TypeVisitor(name, importContext)
    val moduleWithFqNames = typeVisitor.lowerRoot(this, NodeOwner(this, null))

    return moduleWithFqNames.copy(
            imports = (typeVisitor.resolvedImports + moduleWithFqNames.imports).toMutableList(),
            submodules = moduleWithFqNames.submodules.map { submodule -> submodule.addImports() }
    )
}

fun SourceSetModel.addImports(): SourceSetModel = transform { it.addImports() }