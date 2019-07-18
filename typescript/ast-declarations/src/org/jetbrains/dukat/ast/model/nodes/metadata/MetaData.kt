package org.jetbrains.dukat.ast.model.nodes.metadata

import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


//TODO: it's ParameterValueDeclaration for historical reasons, should got rid of this inheritance ASAP
interface MetaData : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = raiseConcern("MetaData has nullable only for historical reasons and will be removed") { false }
    override var meta: ParameterValueDeclaration?
        get() = raiseConcern("MetaData has nullable only for historical reasons and will be removed") { null }
        set(value) {}
}