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
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.ChainCallModel
import org.jetbrains.dukat.astModel.statements.IndexStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.StatementCallModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.ownerContext.NodeOwner

private val CONTAINS_ONLY_UNDERSCORES = "_+".toRegex()
private val STARTS_WITH_NUMBER = "^\\d+".toRegex()

private val RESERVED_WORDS = setOf(
        "as",
        "class",
        "for",
        "fun",
        "in",
        "interface",
        "in",
        "is",
        "object",
        "package",
        "return",
        "this",
        "throw",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when"
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
    return when(this) {
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

private class EscapeIdentificators : ModelWithOwnerTypeLowering {

    private fun StatementCallModel.escape(): StatementCallModel {
        return copy(
                value = value.renameAsParameter(),
                params = params?.map { it.copy(value = it.value.renameAsParameter()) },
                typeParameters = typeParameters.map { it.escape() }
        )
    }

    private fun StatementModel.escape(): StatementModel {
        return when (this) {
            is StatementCallModel -> escape()
            is ReturnStatementModel -> copy(statement = statement.escape())
            is ChainCallModel -> copy(left = left.escape(), right = right.escape())
            is AssignmentStatementModel -> copy(left = left.escape(), right = right.escape())
            is IndexStatementModel -> copy(array = array.escape(), index = index.escape())
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

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>): FunctionModel {
        val declaration = ownerContext.node
        return super.lowerFunctionModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape(),
                body = declaration.body.map { it.escape() }
        )))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>): VariableModel {
        val declaration = ownerContext.node
        return super.lowerVariableModel(ownerContext.copy(node = declaration.copy(name = declaration.name.escape())))
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        val paramName = declaration.name.renameAsParameter()
        return super.lowerParameterModel(ownerContext.copy(node = declaration.copy(name = paramName)))
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>): InterfaceModel {
        val declaration = ownerContext.node
        return super.lowerInterfaceModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape()
        )))
    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val declaration = ownerContext.node

        return super.lowerClassModel(ownerContext.copy(node = declaration.copy(
                name = declaration.name.escape()
        )))
    }

    override fun lowerTopLevelModel(ownerContext: NodeOwner<TopLevelModel>, moduleModel: ModuleModel): TopLevelModel {
        return when (val declaration = ownerContext.node) {
            is EnumModel -> declaration.copy(values = declaration.values.map { value -> value.copy(value = value.value.escape()) })
            else -> super.lowerTopLevelModel(ownerContext, moduleModel)
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

fun ModuleModel.escapeIdentificators(): ModuleModel {
    return EscapeIdentificators().lowerRoot(this, NodeOwner(this, null))
}

fun SourceSetModel.escapeIdentificators() = transform { it.escapeIdentificators() }