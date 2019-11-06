package org.jetbrains.dukat.js.type.analysis

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.js.type.constraint.unresolved.ClassConstraint
import org.jetbrains.dukat.js.type.constraint.container.ConstraintContainer
import org.jetbrains.dukat.js.type.constraint.unresolved.FunctionConstraint
import org.jetbrains.dukat.js.type.constraint.property_owner.PropertyOwner
import org.jetbrains.dukat.js.type.constraint.property_owner.Scope
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
import org.jetbrains.dukat.tsmodel.ReturnStatementDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

fun FunctionDeclaration.addTo(owner: PropertyOwner) {
    if (this.body != null) {
        val functionScope = Scope()

        val parameterConstraintContainers = MutableList(parameters.size) { i ->
            // Store constraints of parameters in scope,
            // and in parameter list (in case the variable is replaced)
            val parameterConstraintContainer = ConstraintContainer()
            functionScope[parameters[i].name] = parameterConstraintContainer
            parameters[i].name to parameterConstraintContainer
        }

        val returnTypeConstraints = body!!.calculateConstraints(functionScope)

        owner[name] = ConstraintContainer(FunctionConstraint(
                returnConstraints = returnTypeConstraints,
                parameterConstraints = parameterConstraintContainers
        ))
    }
}

/*fun ConstructorDeclaration.addTo(owner: PropertyOwner) {
    // TODO add body to constructor in AST and process it like a function
}

fun InterfaceDeclaration.addTo(owner: PropertyOwner) {
    val scope = Scope<ConstraintContainer>()

    for(member in members) {
        member.calculateConstraints(scope)
    }

    //owner[name] = scope
}*/

fun MemberDeclaration.addTo(owner: PropertyOwner) {
    when (this) {
        is FunctionDeclaration -> this.addTo(owner)
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun MemberDeclaration.addToClass(owner: ClassConstraint) {
    when (this) {
        is FunctionDeclaration -> if (this.modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)) { this.addTo(owner) } else { this.addTo(owner.prototype) }
        else -> raiseConcern("Unexpected member entity type <${this::class}>") { this }
    }
}

fun ClassDeclaration.addTo(owner: PropertyOwner) {
    val className = name // Needed for smart cast
    if(className is IdentifierEntity) {
        val classConstraint = ClassConstraint()

        members.forEach { it.addToClass(classConstraint) }

        owner[className.value] = ConstraintContainer(classConstraint)
    } else {
        raiseConcern("Cannot convert class with name of type <${className::class}>.") {  }
    }
}

fun BlockDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    return statements.calculateConstraints(owner)
}

fun VariableDeclaration.addTo(owner: PropertyOwner) {
    owner[name] = initializer.calculateConstraints(owner)
}

fun ExpressionStatementDeclaration.calculateConstraints(owner: PropertyOwner) {
    expression.calculateConstraints(owner)
}

fun ReturnStatementDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    return ConstraintContainer(expression.calculateConstraints(owner))
}

fun TopLevelDeclaration.calculateConstraints(owner: PropertyOwner) : ConstraintContainer? {
    var returnTypeConstraints: ConstraintContainer? = null

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

fun List<TopLevelDeclaration>.calculateConstraints(owner: PropertyOwner) : ConstraintContainer {
    var returnTypeConstraints = ConstraintContainer()

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