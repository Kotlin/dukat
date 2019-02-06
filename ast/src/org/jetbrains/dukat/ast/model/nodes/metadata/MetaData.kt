package org.jetbrains.dukat.ast.model.nodes.metadata

import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration


//TODO: it's ParameterValueDeclaration for historical reasons, should got rid of this inheritance ASAP
interface MetaData : ParameterValueDeclaration {
    override val nullable: Boolean
        get() = throw Exception("MetaData has nullable only for historical reasons and will be removed")
    override var meta: ParameterValueDeclaration?
        get() = throw Exception("MetaData has nullable only for historical reasons and will be removed")
        set(value) {}
}