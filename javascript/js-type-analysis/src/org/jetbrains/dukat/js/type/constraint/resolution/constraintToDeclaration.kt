package org.jetbrains.dukat.js.type.constraint.resolution

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.unresolved.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.unresolved.FunctionConstraint
import org.jetbrains.dukat.js.type.type.anyNullableType
import org.jetbrains.dukat.js.type.type.booleanType
import org.jetbrains.dukat.js.type.type.numberType
import org.jetbrains.dukat.js.type.type.stringType
import org.jetbrains.dukat.js.type.type.unitType
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

val EXPORT_MODIFIERS = listOf(ModifierDeclaration.EXPORT_KEYWORD)
val STATIC_MODIFIERS = listOf(ModifierDeclaration.STATIC_KEYWORD)

fun getVariableDeclaration(name: String, type: ParameterValueDeclaration) = VariableDeclaration(
    name = name,
    type = type,
    modifiers = EXPORT_MODIFIERS,
    initializer = null,
    uid = getUID()
)

fun ConstraintContainer.toParameterDeclaration(name: String) = ParameterDeclaration(
        name = name,
        type = this.toType(),
        initializer = null,
        vararg = false,
        optional = false
)

fun FunctionConstraint.toDeclaration(name: String) = FunctionDeclaration(
        name = name,
        parameters = parameterConstraints.map { (name, constraint) -> constraint.toParameterDeclaration(name) },
        type = returnConstraints.toType(unitType),
        typeParameters = emptyList(),
        modifiers = EXPORT_MODIFIERS,
        body = null,
        uid = getUID()
)

fun ConstraintContainer.toMemberDeclaration(name: String, isStatic: Boolean) : MemberDeclaration? {
    return when (val declaration = toDeclaration(name)) {
        is FunctionDeclaration -> return declaration.copy(modifiers = if (isStatic) STATIC_MODIFIERS else emptyList())
        else -> raiseConcern("Cannot convert declaration of type <${declaration?.let {declaration::class} ?: "null"} to member.>") { null }
    }
}

fun ClassConstraint.toDeclaration(name: String) : ClassDeclaration {
    val members = mutableListOf<MemberDeclaration>()

    members.addAll(
            prototype.propertyNames.mapNotNull { memberName ->
                prototype[memberName]?.toMemberDeclaration(name = memberName, isStatic = false)
            }
    )

    members.addAll(
            propertyNames.mapNotNull { memberName ->
                this[memberName]?.toMemberDeclaration(name = memberName, isStatic = true)
            }
    )

    return ClassDeclaration(
            name = IdentifierEntity(name),
            members = members,
            typeParameters = emptyList(),
            parentEntities = emptyList(), //TODO support inheritance,
            modifiers = EXPORT_MODIFIERS,
            uid = getUID()
    )
}

fun List<Constraint>.toType() : TypeDeclaration? {
    return when {
        this.contains(NumberTypeConstraint) -> numberType
        this.contains(BigIntTypeConstraint) -> numberType
        this.contains(BooleanTypeConstraint) -> booleanType
        this.contains(StringTypeConstraint) -> stringType
        this.isEmpty() -> null
        else -> anyNullableType
    }
}

fun ConstraintContainer.toType(defaultType: TypeDeclaration = anyNullableType) : TypeDeclaration {
    val flatConstraints = getFlatConstraints()
    return flatConstraints.toType() ?: defaultType
}

fun ConstraintContainer.toDeclaration(name: String) : TopLevelDeclaration? {
    var declaration: TopLevelDeclaration? = null

    val flatConstraints = getFlatConstraints()

    flatConstraints.forEach {
        when (it) {
            is ClassConstraint -> declaration = it.toDeclaration(name)
            is FunctionConstraint -> declaration = it.toDeclaration(name)
        }
    }

    if(declaration == null) {
        val type = flatConstraints.toType()

        if(type != null) {
            declaration = getVariableDeclaration(name, type)
        }
    }

    return declaration
}

fun Constraint.toDeclaration(name: String) : TopLevelDeclaration? {
    return when (this) {
        is ConstraintContainer -> this.toDeclaration(name)
        else -> raiseConcern("Export cannot be defined by constraint directly.") { null }
    }
}