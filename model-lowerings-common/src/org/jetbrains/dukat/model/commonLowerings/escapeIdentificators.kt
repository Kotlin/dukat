package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap

private val CONTAINS_ONLY_UNDERSCORES = "_+".toRegex()
private val STARTS_WITH_NUMBER = "^\\d+".toRegex()

private val RESERVED_WORDS = setOf(
    "as",
    "break",
    "class",
    "continue",
    "do",
    "else",
    "false",
    "for",
    "fun",
    "if",
    "in",
    "interface",
    "is",
    "null",
    "object",
    "package",
    "return",
    "super",
    "this",
    "throw",
    "true",
    "try",
    "typealias",
    "typeof",
    "val",
    "var",
    "when",
    "while"
)

private val RENAME_PARAM_MAP = mapOf(
    Pair("this", "self"),

    Pair("as", "param_as"),
    Pair("class", "param_class"),
    Pair("fun", "param_fun"),
    Pair("in", "param_in"),
    Pair("interface", "param_interface"),
    Pair("in", "param_in"),
    Pair("is", "param_is"),
    Pair("object", "obj"),
    Pair("package", "param_package"),
    Pair("return", "param_return"),
    Pair("throw", "param_throw"),
    Pair("try", "param_try"),
    Pair("typealias", "param_typealias"),
    Pair("val", "param_val"),
    Pair("when", "param_when")
)

private fun String.renameAsParameter(): String {
    return RENAME_PARAM_MAP[this] ?: this.escape()
}

private fun NameEntity.renameAsParameter(): NameEntity {
    return when (this) {
        is IdentifierEntity -> copy(value = value.renameAsParameter())
        is QualifierEntity -> this
    }
}

private fun Char.isLetterOrDigitOrUnderscore(): Boolean {
    return isLetterOrDigit() || equals('_')
}

private fun String.isValidKotlinIdentifier(): Boolean {
    return isNotEmpty() &&
            all(Char::isLetterOrDigitOrUnderscore) &&
            (!startsWith('_') || any { it != '_' })
}

private fun String.isEscaped(): Boolean {
    return startsWith('`') && endsWith('`')
}

private fun String.isReservedKeyword(): Boolean {
    return RESERVED_WORDS.contains(this)
}

private fun String.shouldEscape(): Boolean {
    return !isEscaped() && (isReservedKeyword() || !isValidKotlinIdentifier())
}

private fun String.escape(): String {
    return if (shouldEscape()) {
        "`${this}`"
    } else {
        this
    }
}

private fun IdentifierEntity.escape(): IdentifierEntity {
    return if (value.shouldEscape()) {
        copy(value = value.escape())
    } else {
        this
    }
}

private fun QualifierEntity.escape(): QualifierEntity {
    return QualifierEntity(left.escape(), right.escape())
}

fun NameEntity.escape(): NameEntity {
    return when (this) {
        is IdentifierEntity -> escape()
        is QualifierEntity -> escape()
    }
}

private class EscapeIdentificatorsTypeLowering : ModelWithOwnerTypeLowering {

    override fun lower(expression: ExpressionModel): ExpressionModel {
        return super.lower(expression).let {
            when (it) {
                is IdentifierExpressionModel -> it.copy(identifier = it.identifier.renameAsParameter().escape())
                else -> it
            }
        }
    }

    override fun lowerTypeValueModel(ownerContext: NodeOwner<TypeValueModel>): TypeValueModel {
        return super.lowerTypeValueModel(
            ownerContext.copy(
                node = ownerContext.node.copy(
                    value = ownerContext.node.value.escape(),
                    fqName = ownerContext.node.fqName?.escape()
                )
            )
        )
    }

    override fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        return super.lowerPropertyModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        return super.lowerMethodModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val declaration = ownerContext.node
        return super.lowerFunctionModel(
            ownerContext.copy(
                node = declaration.copy(
                    name = declaration.name.escape()
                )
            ), parentModule
        )
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel?): VariableModel {
        val declaration = ownerContext.node
        return super.lowerVariableModel(
            ownerContext.copy(node = declaration.copy(name = declaration.name.escape())),
            parentModule
        )
    }


    override fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel {
        val declaration = ownerContext.node
        val paramName = declaration.name?.renameAsParameter()
        return super.lowerLambdaParameterModel(ownerContext.copy(node = declaration.copy(name = paramName)))
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        val paramName = declaration.name.renameAsParameter()
        return super.lowerParameterModel(ownerContext.copy(node = declaration.copy(name = paramName)))
    }


    override fun lowerInterfaceModel(
        ownerContext: NodeOwner<InterfaceModel>,
        parentModule: ModuleModel
    ): InterfaceModel {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(
            ownerContext.copy(
                node = declaration.copy(
                    name = declaration.name.escape()
                )
            ), parentModule
        )
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val declaration = ownerContext.node

        return super.lowerClassModel(
            ownerContext.copy(
                node = declaration.copy(
                    name = declaration.name.escape()
                )
            ), parentModule
        )
    }

    override fun lowerTypeAliasModel(
        ownerContext: NodeOwner<TypeAliasModel>,
        parentModule: ModuleModel
    ): TypeAliasModel {
        val declaration = ownerContext.node

        return super.lowerTypeAliasModel(
            ownerContext.copy(
                node = declaration.copy(
                    name = declaration.name.escape()
                )
            ), parentModule
        )
    }

    override fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>, parentModule: ModuleModel): EnumModel {
        val declaration = ownerContext.node
        return declaration.copy(values = declaration.values.map { value -> value.copy(value = value.value.escape()) })
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        return moduleModel.copy(
            name = moduleModel.name.escape(),
            shortName = moduleModel.shortName.escape(),
            declarations = lowerTopLevelDeclarations(moduleModel.declarations, ownerContext, moduleModel),
            submodules = moduleModel.submodules.map { submodule -> lowerRoot(submodule, ownerContext.wrap(submodule)) }
        )
    }
}

class EscapeIdentificators : ModelLowering {
    override fun lower(module: ModuleModel): ModuleModel {
        return EscapeIdentificatorsTypeLowering().lowerRoot(module, NodeOwner(module, null))
    }
}

