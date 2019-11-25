package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.model.commonLowerings.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private class TypeVisitor(private val visit: (TypeValueModel) -> Unit) : ModelWithOwnerTypeLowering {
    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        visit(ownerContext.node)
        return super.lowerTypeValueModel(ownerContext)
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

private fun TypeValueModel.isFqReference(): Boolean {
    if (value == fqName) {
        return true
    } else {
        val leftMost = fqName?.leftMost()
        if ((leftMost == IdentifierEntity("<ROOT>")) || (leftMost == IdentifierEntity("<LIBROOT>"))) {
            if (value is QualifierEntity) {
                if (value == fqName?.shiftLeft()) {
                    return true
                }
            }
        }
    }

    return false
}

private fun ModuleModel.addImports(): ModuleModel {
    val resolveImports = linkedSetOf<NameEntity>()
    TypeVisitor { typeModel ->
        typeModel.fqName?.shiftRight()?.let {
            if (it != name) {
                if (!typeModel.isFqReference()) {
                    resolveImports.add(typeModel.fqName!!)
                }
            }
        }
    }.lowerRoot(this, NodeOwner(this, null))

    return copy(
            imports = (resolveImports + imports).toMutableList(),
            submodules = submodules.map { submodule -> submodule.addImports() }
    )
}

fun SourceSetModel.addImports(): SourceSetModel = transform { it.addImports() }