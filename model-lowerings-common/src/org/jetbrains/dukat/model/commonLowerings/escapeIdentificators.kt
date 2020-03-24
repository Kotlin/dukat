package org.jetbrains.dukat.model.commonLowerings

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
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.astModel.visitors.LambdaParameterModel
import org.jetbrains.dukat.ownerContext.NodeOwner

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

private fun String.shouldEscape(): Boolean {
    val isReservedWord = RESERVED_WORDS.contains(this)
    val containsDollarSign = this.contains("$")
    val containsMinusSign = this.contains("-")
    val containsOnlyUnderscores = CONTAINS_ONLY_UNDERSCORES.matches(this)
    val startsWithNumber = this.contains(STARTS_WITH_NUMBER)
    val isEscapedAlready = this.startsWith("`")
    val isStartingWithColon = this.startsWith(":")
    val isStartingWithDot = this.startsWith(".")

    return !isEscapedAlready &&
            (isReservedWord || containsDollarSign || containsOnlyUnderscores || containsMinusSign || startsWithNumber || isStartingWithColon || isStartingWithDot)
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

    private fun CallExpressionModel.escape(): CallExpressionModel {
        return copy(
                expression = expression.escape(),
                arguments = arguments.map { it.escape() },
                typeParameters = typeParameters.map { it.escape() }
        )
    }

    private fun ExpressionModel.escape(): ExpressionModel {
        return when (this) {
            is IdentifierExpressionModel -> copy(identifier = identifier.renameAsParameter())
            is CallExpressionModel -> escape()
            is PropertyAccessExpressionModel -> copy(left = left.escape(), right = right.escape())
            is IndexExpressionModel -> copy(array = array.escape(), index = index.escape())
            is ThisExpressionModel -> this
            else -> this
        }
    }

    private fun StatementModel.escape(): StatementModel {
        return when (this) {
            is ExpressionStatementModel -> copy(expression = expression.escape())
            is ReturnStatementModel -> copy(expression = expression?.escape())
            is AssignmentStatementModel -> copy(left = left.escape(), right = right.escape())
            else -> this
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
        return super.lowerFunctionModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape(),
                body = declaration.body.map { it.escape() }
        )), parentModule)
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel): VariableModel {
        val declaration = ownerContext.node
        return super.lowerVariableModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())), parentModule)
    }


    override fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel {
        val declaration = ownerContext.node
        val paramName = declaration.name.renameAsParameter()
        return super.lowerLambdaParameterModel(ownerContext.copy(node = declaration.copy(name = paramName)))
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        val paramName = declaration.name.renameAsParameter()
        return super.lowerParameterModel(ownerContext.copy(node = declaration.copy(name = paramName)))
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape()
        )), parentModule)
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val declaration = ownerContext.node

        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape()
        )), parentModule)
    }

    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, parentModule: ModuleModel): TopLevelModel {
        return when (val declaration = ownerContext.node) {
            is EnumModel -> declaration.copy(values = declaration.values.map { value -> value.copy(value = value.value.escape()) })
            else -> super.lowerTopLevelModel(ownerContext, parentModule)
        }
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

private fun ModuleModel.escapeIdentificators(): ModuleModel {
    return EscapeIdentificatorsTypeLowering().lowerRoot(this, NodeOwner(this, null))
}

private fun SourceSetModel.escapeIdentificators() = transform { it.escapeIdentificators() }

class EscapeIdentificators() : ModelLowering {
    override fun lower(source: SourceSetModel): SourceSetModel {
        return source.escapeIdentificators()
    }
}

