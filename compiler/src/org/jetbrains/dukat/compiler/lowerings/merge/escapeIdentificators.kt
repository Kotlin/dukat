package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.compiler.lowerings.model.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner

private fun escapeIdentificator(identificator: String): String {
    val reservedWords = setOf(
            "as",
            "fun",
            "in",
            "interface",
            "is",
            "object",
            "package",
            "typealias",
            "typeof",
            "val",
            "var",
            "when"
    )

    val isReservedWord = reservedWords.contains(identificator)
    val containsDollarSign = identificator.contains("$")
    val containsOnlyUnderscores = "^_+$".toRegex().containsMatchIn(identificator)
    val isEscapedAlready = "^`.*`$".toRegex().containsMatchIn(identificator)

    return if (!isEscapedAlready && (isReservedWord || containsDollarSign || containsOnlyUnderscores)) {
        "`${identificator}`"
    } else {
        identificator
    }
}


private class EscapeIdentificators : ModelWithOwnerTypeLowering {

    private fun IdentifierEntity.escape(): IdentifierEntity {
        return copy(value = escapeIdentificator(value))
    }

    private fun TypeValueModel.escape(): TypeValueModel {
        return when (val typeNodeValue = value) {
            is IdentifierEntity -> copy(value = typeNodeValue.escape())
            is QualifierEntity -> copy(value = typeNodeValue.escape())
        }
    }

    private fun QualifierEntity.escape(): QualifierEntity {
        return when (val nodeLeft = left) {
            is IdentifierEntity -> QualifierEntity(nodeLeft.escape(), right.escape())
            is QualifierEntity -> nodeLeft.copy(left = nodeLeft.escape(), right = right.escape())
        }
    }

    private fun NameEntity.escape(): NameEntity {
        return when (this) {
            is IdentifierEntity -> escape()
            is QualifierEntity -> escape()
        }
    }

    override fun lowerTypeModel(ownerContext: NodeOwner<TypeModel>): TypeModel {
        val declaration = ownerContext.node
        return when (declaration) {
            is TypeValueModel -> declaration.escape()
            else -> {
                super.lowerTypeModel(ownerContext)
            }
        }
    }

    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        return super.lowerPropertyModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        return super.lowerMethodModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        val declaration = ownerContext.node
        return super.lowerFunctionModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        val declaration = ownerContext.node
        return super.lowerVariableModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        return super.lowerParameterModel(ownerContext.copy(node = declaration.copy(name = escapeIdentificator(declaration.name))))
    }

    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape(),
                typeParameters = declaration.typeParameters.map { typeParameter -> typeParameter.copy(name = typeParameter.name.escape()) }
        )))
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val declaration = ownerContext.node
        return super.lowerClassModel(
                ownerContext.copy(node = declaration.copy(
                        name = declaration.name.escape(),
                        typeParameters = declaration.typeParameters.map { typeParameter -> typeParameter.copy(name = typeParameter.name.escape()) }
                )))
    }


    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>): TopLevelModel {
        val declaration = ownerContext.node
        return when (declaration) {
            is EnumModel -> declaration.copy(values = declaration.values.map { value -> value.copy(value = escapeIdentificator(value.value)) })
            else -> super.lowerTopLevelModel(ownerContext)
        }
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
                name = moduleModel.name.escape(),
                shortName = moduleModel.shortName.escape(),
                declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext),
                submodules = moduleModel.submodules.map { submodule -> lowerRoot(submodule, ownerContext.wrap(submodule)) }
        )
    }
}

fun ModuleModel.escapeIdentificators(): ModuleModel {
    return EscapeIdentificators().lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.escapeIdentificators() = transform { it.escapeIdentificators() }