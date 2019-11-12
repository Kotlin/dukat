package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.Constraint
import org.jetbrains.dukat.js.type.constraint.properties.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.composite.CompositeConstraint
import org.jetbrains.dukat.js.type.constraint.immutable.resolved.BooleanTypeConstraint
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
import org.jetbrains.dukat.tsmodel.IfStatementDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

fun FunctionDeclaration.addTo(owner: PropertyOwner) {
    if (this.body != null) {
        val pathWalker = PathWalker()

        val versions = mutableListOf<FunctionConstraint>()

        do {
            val functionScope = Scope()

            val parameterConstraints = MutableList(parameters.size) { i ->
                // Store constraints of parameters in scope,
                // and in parameter list (in case the variable is replaced)
                val parameterConstraint = CompositeConstraint()
                functionScope[parameters[i].name] = parameterConstraint
                parameters[i].name to parameterConstraint
            }

            val returnTypeConstraints = body!!.calculateConstraints(functionScope, pathWalker) ?: VoidTypeConstraint

            versions.add(FunctionConstraint(
                    returnConstraints = returnTypeConstraints,
                    parameterConstraints = parameterConstraints
            ))
        } while (pathWalker.startNextPath())

        //TODO unify versions of function

        owner[name] = versions[0]
    }
}

/*
fun ConstructorDeclaration.addTo(owner: PropertyOwner) {
    // TODO add body to constructor in AST and process it like a function
}

fun InterfaceDeclaration.addTo(owner: PropertyOwner)
*/

fun PropertyDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    owner[name] = initializer?.calculateConstraints(owner, path) ?: VoidTypeConstraint
}

fun MemberDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is PropertyDeclaration -> this.addTo(owner, path)
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun MemberDeclaration.addToClass(owner: ClassConstraint, path: PathWalker) {
    when (this) {
        is FunctionDeclaration -> if (this.modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)) { this.addTo(owner) } else { this.addTo(owner.prototype) }
        is PropertyDeclaration -> this.addTo(owner, path)
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun ClassDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    val className = name // Needed for smart cast
    if(className is IdentifierEntity) {
        val classConstraint = ClassConstraint()

        members.forEach { it.addToClass(classConstraint, path) }

        owner[className.value] = classConstraint
    } else {
        raiseConcern("Cannot convert class with name of type <${className::class}>.") {  }
    }
}

fun BlockDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    return statements.calculateConstraints(owner, path)
}

fun VariableDeclaration.addTo(owner: PropertyOwner, path: PathWalker) {
    owner[name] = initializer?.calculateConstraints(owner, path) ?: VoidTypeConstraint
}

fun IfStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    condition.calculateConstraints(owner, path) += BooleanTypeConstraint

    return when (path.getNextDirection()) {
        PathWalker.Direction.First -> thenStatement.calculateConstraints(owner, path)
        PathWalker.Direction.Second -> elseStatement?.calculateConstraints(owner, path)
    }
}

fun ExpressionStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) {
    expression.calculateConstraints(owner, path)
}

fun ReturnStatementDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint {
    return expression?.calculateConstraints(owner, path) ?: VoidTypeConstraint
}

fun TopLevelDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    var returnTypeConstraints: Constraint? = null

    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        is ClassDeclaration -> this.addTo(owner, path)
        is VariableDeclaration -> this.addTo(owner, path)
        is IfStatementDeclaration -> returnTypeConstraints = this.calculateConstraints(owner, path)
        is ExpressionStatementDeclaration -> this.calculateConstraints(owner, path)
        is BlockDeclaration -> returnTypeConstraints = this.calculateConstraints(owner, path)
        is ReturnStatementDeclaration -> returnTypeConstraints = this.calculateConstraints(owner, path)
        is InterfaceDeclaration,
        is ModuleDeclaration -> { /* These statements aren't supported in JS (ignore them) */ }
        else -> raiseConcern("Unexpected top level entity type <${this::class}>") {  }
    }

    return returnTypeConstraints
}

fun List<TopLevelDeclaration>.calculateConstraints(owner: PropertyOwner, path: PathWalker) : Constraint? {
    this.forEach { statement ->
        val statementReturnTypeConstraints = statement.calculateConstraints(owner, path)

        if(statementReturnTypeConstraints != null) {
            return statementReturnTypeConstraints
        }
    }

    return null
}

fun ModuleDeclaration.calculateConstraints(owner: PropertyOwner, path: PathWalker) = declarations.calculateConstraints(owner, path)

fun ModuleDeclaration.introduceTypes(exportResolver: ExportResolver) : ModuleDeclaration {
    val scope = Scope()
    val pathWalker = PathWalker()
    calculateConstraints(scope, pathWalker)
    //TODO check other paths
    val declarations = exportResolver.resolve(scope)
    return copy(declarations = declarations)
}

fun SourceFileDeclaration.introduceTypes(exportResolver: ExportResolver) = copy(root = root.introduceTypes(exportResolver))

fun SourceSetDeclaration.introduceTypes(exportResolver: ExportResolver = GeneralExportResolver()) = copy(sources = sources.map { it.introduceTypes(exportResolver) })