package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.VoidTypeConstraint
import org.jetbrains.dukat.js.type.constraint.properties.FunctionConstraint
import org.jetbrains.dukat.js.type.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.property_owner.Scope
import org.jetbrains.dukat.js.type.export_resolution.GeneralExportResolver
import org.jetbrains.dukat.js.type.export_resolution.ExportResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.BlockDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ExpressionStatementDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

fun FunctionDeclaration.addTo(owner: PropertyOwner) {
    if (this.body != null) {
        val functionScope = Scope()

        val parameterConstraints = MutableList(parameters.size) { i ->
            // Store constraints of parameters in scope,
            // and in parameter list (in case the variable is replaced)
            val parameterConstraint = CompositeConstraint()
            functionScope[parameters[i].name] = parameterConstraint
            parameters[i].name to parameterConstraint
        }

        val returnTypeConstraints = body!!.calculateConstraints(functionScope)

        owner[name] = FunctionConstraint(
                returnConstraints = returnTypeConstraints,
                parameterConstraints = parameterConstraints
        )
    }
}

/*
fun ConstructorDeclaration.addTo(owner: PropertyOwner) {
    // TODO add body to constructor in AST and process it like a function
}

fun InterfaceDeclaration.addTo(owner: PropertyOwner)
*/

fun PropertyDeclaration.addTo(owner: PropertyOwner) {
    owner[name] = initializer?.calculateConstraints(owner) ?: VoidTypeConstraint
}

fun MemberDeclaration.addTo(owner: PropertyOwner) {
    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is PropertyDeclaration -> this.addTo(owner)
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun MemberDeclaration.addToClass(owner: ClassConstraint) {
    when (this) {
        is FunctionDeclaration -> if (this.modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)) { this.addTo(owner) } else { this.addTo(owner.prototype) }
        is PropertyDeclaration -> this.addTo(owner)
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun ClassDeclaration.addTo(owner: PropertyOwner) {
    val className = name // Needed for smart cast
    if(className is IdentifierEntity) {
        val classConstraint = ClassConstraint()

        members.forEach { it.addToClass(classConstraint) }

        owner[className.value] = classConstraint
    } else {
        raiseConcern("Cannot convert class with name of type <${className::class}>.") {  }
    }
}

fun BlockDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    return statements.calculateConstraints(owner)
}

fun VariableDeclaration.addTo(owner: PropertyOwner) {
    owner[name] = initializer?.calculateConstraints(owner) ?: VoidTypeConstraint
}

fun ExpressionStatementDeclaration.calculateConstraints(owner: PropertyOwner) {
    expression.calculateConstraints(owner)
}

fun ReturnStatementDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint {
    return expression?.calculateConstraints(owner) ?: VoidTypeConstraint
}

fun TopLevelDeclaration.calculateConstraints(owner: PropertyOwner) : Constraint? {
    var returnTypeConstraints: Constraint? = null

    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is ClassDeclaration -> this.addTo(owner)
        is VariableDeclaration -> this.addTo(owner)
        is ExpressionStatementDeclaration -> this.calculateConstraints(owner)
        is BlockDeclaration -> returnTypeConstraints = this.calculateConstraints(owner)
        is ReturnStatementDeclaration -> returnTypeConstraints = this.calculateConstraints(owner)
        is InterfaceDeclaration,
        is ModuleDeclaration -> {  }
        else -> raiseConcern("Unexpected top level entity type <${this::class}>") {  }
    }

    return returnTypeConstraints
}

fun List<TopLevelDeclaration>.calculateConstraints(owner: PropertyOwner) : Constraint {
    var returnTypeConstraints: Constraint = VoidTypeConstraint

    for(statement in this) {
        val statementReturnTypeConstraints = statement.calculateConstraints(owner)

        if(statementReturnTypeConstraints != null) {
            // This should only happen once per code block
            returnTypeConstraints = statementReturnTypeConstraints
        }
    }

    return returnTypeConstraints
}

fun ModuleDeclaration.calculateConstraints(owner: PropertyOwner) = declarations.calculateConstraints(owner)

fun ModuleDeclaration.introduceTypes(exportResolver: ExportResolver) : ModuleDeclaration {
    val scope = Scope()
    calculateConstraints(scope)
    val declarations = exportResolver.resolve(scope)
    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(exportResolver: ExportResolver) = copy(root = root.introduceTypes(exportResolver))

fun SourceSetDeclaration.introduceTypes(exportResolver: ExportResolver = GeneralExportResolver()) = copy(sources = sources.map { it.introduceTypes(exportResolver) })