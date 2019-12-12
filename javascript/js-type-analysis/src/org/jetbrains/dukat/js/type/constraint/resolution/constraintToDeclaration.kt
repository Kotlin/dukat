package org.jetbrains.dukat.js.type.constraint.resolution

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.composite.UnionTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.CallableConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BigIntTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NoTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.NumberTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.StringTypeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.ThrowConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.properties.ObjectConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.type.anyNullableType
import org.jetbrains.dukat.js.type.type.booleanType
import org.jetbrains.dukat.js.type.type.nothingType
import org.jetbrains.dukat.js.type.type.numberType
import org.jetbrains.dukat.js.type.type.stringType
import org.jetbrains.dukat.js.type.type.voidType
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ExportAssignmentDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithUidDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private val DECLARE_MODIFIERS = listOf(ModifierDeclaration.DECLARE_KEYWORD)
private val EXPORT_MODIFIERS = listOf(ModifierDeclaration.EXPORT_KEYWORD)
private val STATIC_MODIFIERS = listOf(ModifierDeclaration.STATIC_KEYWORD)

private fun getVariableDeclaration(name: String, type: ParameterValueDeclaration, exportModifiers: List<ModifierDeclaration>) = VariableDeclaration(
        name = name,
        type = type,
        modifiers = exportModifiers,
        initializer = null,
        uid = getUID()
)

private fun getPropertyDeclaration(name: String, type: ParameterValueDeclaration, isStatic: Boolean) = PropertyDeclaration(
        name = name,
        initializer = null,
        type = type,
        typeParameters = emptyList(),
        optional = false,
        modifiers = if (isStatic) STATIC_MODIFIERS else emptyList()
)

private fun Constraint.toParameterDeclaration(name: String) = ParameterDeclaration(
        name = name,
        type = this.toType(),
        initializer = null,
        vararg = false,
        optional = false
)

private fun FunctionConstraint.toDeclaration(name: String, exportModifiers: List<ModifierDeclaration>) = FunctionDeclaration(
        name = name,
        parameters = parameterConstraints.map { (name, constraint) -> constraint.toParameterDeclaration(name) },
        type = returnConstraints.toType(),
        typeParameters = emptyList(),
        modifiers = exportModifiers,
        body = null,
        uid = getUID()
)

private fun FunctionConstraint.toConstructor() = ConstructorDeclaration(
        parameters = parameterConstraints.map { (name, constraint) -> constraint.toParameterDeclaration(name) },
        typeParameters = emptyList(),
        modifiers = emptyList(),
        body = null
)

private fun FunctionConstraint.toCallSignature() = CallSignatureDeclaration(
        parameters = parameterConstraints.map { (name, constraint) -> constraint.toParameterDeclaration(name) },
        type = returnConstraints.toType(),
        typeParameters = emptyList()
)

private fun CallableConstraint.toCallSignature() = CallSignatureDeclaration(
        parameters = List(parameterCount) { NoTypeConstraint.toParameterDeclaration("") },
        type = returnConstraints.toType(),
        typeParameters = emptyList()
)

private fun FunctionDeclaration.withStaticModifier(isStatic: Boolean) : FunctionDeclaration {
    return this.copy(modifiers = if (isStatic) STATIC_MODIFIERS else emptyList())
}

private fun FunctionConstraint.toMemberDeclaration(name: String, isStatic: Boolean) : MemberDeclaration? {
    return this.toDeclaration(name, EXPORT_MODIFIERS).withStaticModifier(isStatic)
}

private fun Constraint.toMemberDeclaration(name: String, isStatic: Boolean = false) : MemberDeclaration? {
    return when (this) {
        is FunctionConstraint -> this.toMemberDeclaration(name, isStatic)
        else -> getPropertyDeclaration(name, this.toType(), isStatic)
    }
}

private fun ClassConstraint.toDeclaration(name: String, exportModifiers: List<ModifierDeclaration>) : ClassDeclaration {
    val members = mutableListOf<MemberDeclaration>()

    val constructorConstraint = constructorConstraint
    if (constructorConstraint is FunctionConstraint) {
        members.add(constructorConstraint.toConstructor())
    }

    val prototype = this["prototype"]

    if (prototype is ObjectConstraint) {
        members.addAll(
                prototype.propertyNames.mapNotNull { memberName ->
                    prototype[memberName]?.toMemberDeclaration(name = memberName, isStatic = false)
                }
        )
    } else {
        raiseConcern("Class prototype is no object. Conversion might be erroneous!") {  }
    }

    members.addAll(
            propertyNames.mapNotNull { memberName ->
                //Don't output the prototype object
                if (memberName != "prototype") {
                    this[memberName]?.toMemberDeclaration(name = memberName, isStatic = true)
                } else {
                    null
                }
            }
    )

    return ClassDeclaration(
            name = IdentifierEntity(name),
            members = members,
            typeParameters = emptyList(),
            parentEntities = emptyList(),
            modifiers = exportModifiers,
            uid = getUID()
    )
}

private fun FunctionConstraint.toType() : FunctionTypeDeclaration {
    return FunctionTypeDeclaration(
            parameters = parameterConstraints.map { (name, constraint) -> constraint.toParameterDeclaration(name) },
            type = returnConstraints.toType()
    )
}

private fun UnionTypeConstraint.toType() : UnionTypeDeclaration {
    return UnionTypeDeclaration(
            params = types.map { it.toType() }
    )
}

private fun ObjectConstraint.mapMembers() : List<MemberDeclaration> {
    val members = mutableListOf<MemberDeclaration>()

    callSignatureConstraints.forEach {
        when (it) {
            is FunctionConstraint -> members.add(it.toCallSignature())
            is CallableConstraint -> members.add(it.toCallSignature())
        }
    }

    members.addAll(
            propertyNames.mapNotNull { memberName ->
                this[memberName]?.toMemberDeclaration(name = memberName)
            }
    )

    if (instantiatedClass is PropertyOwner) {
        val classPrototype = instantiatedClass["prototype"]

        if (classPrototype is ObjectConstraint) {
            members.addAll(classPrototype.mapMembers())
        }
    }

    return members
}

private fun ObjectConstraint.toType() : ObjectLiteralDeclaration {
    return ObjectLiteralDeclaration(
            members = mapMembers(),
            uid = getUID(),
            nullable = true
    )
}

private fun Constraint.toType() : ParameterValueDeclaration {
    return when (this) {
        is NumberTypeConstraint -> numberType
        is BigIntTypeConstraint -> numberType
        is BooleanTypeConstraint -> booleanType
        is StringTypeConstraint -> stringType
        is VoidTypeConstraint -> voidType
        is ThrowConstraint -> nothingType
        is UnionTypeConstraint -> this.toType()
        is ObjectConstraint -> this.toType()
        is FunctionConstraint -> this.toType()
        else -> anyNullableType
    }
}

private fun Constraint.toDeclaration(name: String, exportModifiers: List<ModifierDeclaration>) : TopLevelDeclaration? {
    return when (this) {
        is ClassConstraint -> this.toDeclaration(name, exportModifiers)
        is FunctionConstraint -> this.toDeclaration(name, exportModifiers)
        is CompositeConstraint -> raiseConcern("Unexpected composited type for variable named '$name'. Should be resolved by this point!") { null }
        else -> getVariableDeclaration(name, this.toType(), exportModifiers)
    }
}

fun Constraint.toDeclaration(name: String) = toDeclaration(name, EXPORT_MODIFIERS)

fun Constraint.asDefaultToDeclarations(defaultExportName: String) : List<TopLevelDeclaration> {
    val declaration = this.toDeclaration(defaultExportName, DECLARE_MODIFIERS)

    return if (declaration is WithUidDeclaration) {
        val exportEqualsDeclaration = ExportAssignmentDeclaration(
                declaration.uid,
                true
        )

        listOf(exportEqualsDeclaration, declaration)
    } else {
        emptyList()
    }
}

