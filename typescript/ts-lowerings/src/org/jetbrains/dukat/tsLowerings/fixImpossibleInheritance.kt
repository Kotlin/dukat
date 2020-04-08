package org.jetbrains.dukat.tsLowerings

import TopLevelDeclarationLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration

private class ClassLikeContext(private val classLikeMap: Map<String, ClassLikeDeclaration>) {
    fun getKnownParents(classLike: ClassLikeDeclaration): List<ClassLikeDeclaration> {
        return classLike.parentEntities.mapNotNull {
            it.typeReference?.uid?.let { uid ->
                classLikeMap[uid]
            }
        }
    }
}

private class FixImpossibleInheritanceDeclarationLowering(private val classLikeContext: ClassLikeContext) : TopLevelDeclarationLowering {

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): ClassDeclaration {
        val parents = classLikeContext.getKnownParents(declaration).filterIsInstance(ClassDeclaration::class.java)
        return if (parents.size > 1) {
            val impossibleParents = parents.subList(1, parents.size).map { it.uid }.toSet()

            val parentEntitiesResolved = declaration.parentEntities.filter { parentEntity ->
                !impossibleParents.contains(parentEntity.typeReference?.uid)
            }

            declaration.copy(parentEntities = parentEntitiesResolved)
        } else {
            declaration
        }
    }

}

private fun ModuleDeclaration.visitClassLike(visitor: (ClassLikeDeclaration) -> Unit) {
    declarations.forEach {
        when (it) {
            is ClassLikeDeclaration -> visitor(it)
            is ModuleDeclaration -> it.visitClassLike(visitor)
        }
    }
}

private fun SourceSetDeclaration.visitClassLike(visitor: (ClassLikeDeclaration) -> Unit) {
    sources.forEach { source -> source.root.visitClassLike(visitor) }
}

private fun ModuleDeclaration.fixImpossibleInheritance(classLikeContext: ClassLikeContext): ModuleDeclaration {
    return FixImpossibleInheritanceDeclarationLowering(classLikeContext).lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.fixImpossibleInheritance(classLikeContext: ClassLikeContext): SourceFileDeclaration {
    return copy(root = root.fixImpossibleInheritance(classLikeContext))
}

private fun SourceSetDeclaration.fixImpossibleInheritance(): SourceSetDeclaration {
    val classLikeMap = mutableMapOf<String, ClassLikeDeclaration>()
    visitClassLike {
        classLikeMap[it.uid] = it
    }

    return copy(sources = sources.map { it.fixImpossibleInheritance(ClassLikeContext(classLikeMap)) })
}

class FixImpossibleInheritance() : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.fixImpossibleInheritance()
    }
}