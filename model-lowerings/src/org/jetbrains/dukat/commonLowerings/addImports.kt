package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ImportModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.TypeValueModel
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

private fun NodeOwner<*>.classLikeOwner(): ClassLikeModel? {
    return generateSequence(owner) { it.owner }.firstOrNull { it.node is ClassLikeModel }?.node as? ClassLikeModel
}

private class ImportResolver(private val packageName: NameEntity, private val importContext: MutableMap<NameEntity, NameEntity>) : ModelWithOwnerTypeLowering {
    val resolvedImports = linkedSetOf<ImportModel>()

    private fun resolveName(entityName: NameEntity): NameEntity? {
        val entityPackageName = entityName.shiftRight()
        val shortName = entityName.rightMost()

        return if (!importContext.containsKey(shortName)) {
            if (entityPackageName != null) {
                if (entityPackageName.normalize() != packageName.normalize()) {
                    if (!isStdLibEntity(entityName)) {
                        importContext[shortName] = entityPackageName
                        resolvedImports.add(ImportModel(entityName))
                    }
                }
            }
            shortName
        } else if (importContext[shortName] == entityPackageName) {
            shortName
        } else {
            val conflictingImport = resolvedImports.firstOrNull { (it.name.rightMost() == shortName) && (it.name != entityName) }
            if ((conflictingImport != null) && (entityName.isTopLevelImport())) {
                val alias = IdentifierEntity(entityName.translate().replace(".", "_").filterNot { it == '`' })
                resolvedImports.add(ImportModel(entityName, alias))
                alias
            } else null
        }
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        val node = ownerContext.node
        val fqName = node.fqName

        val valueResolved =
        if (fqName != null) {
            val resolvedFqName = resolveName(fqName)
            if (resolvedFqName != null) {
                resolvedFqName
            } else {
                val classOwner = ownerContext.classLikeOwner()
                val classOwnerName = classOwner?.name
                val classOwnerFqName = classOwnerName?.let { packageName.appendLeft(it) }
                if ((classOwnerFqName != null) && (classOwnerFqName != fqName)) {
                    fqName
                } else {
                    node.value
                }
            }
        } else {
            node.value
        }

        return super.lowerTypeValueModel(ownerContext.copy(node = node.copy(value = valueResolved)))
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

    val nameVisitor = ImportResolver(name, buildMap<NameEntity, NameEntity> {
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