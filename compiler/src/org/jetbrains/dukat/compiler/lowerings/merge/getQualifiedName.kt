package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.processing.appendLeft
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.OwnerContext

@Suppress("UNCHECKED_CAST")
fun OwnerContext.getQualifiedName(): NameEntity {
    val moduleOwners = getOwners()
            .filter { (it is NodeOwner<*>) && (it.node is ModuleModel) }
            .toList() as List<NodeOwner<ModuleModel>>

    return if (moduleOwners.size == 1) {
        moduleOwners.get(0).node.shortName
    } else {
        return moduleOwners
                .drop(1)
                .fold(moduleOwners.first().node.shortName) { acc, a ->
                    a.node.shortName.appendLeft(acc)
                } as QualifierEntity
    }
}
