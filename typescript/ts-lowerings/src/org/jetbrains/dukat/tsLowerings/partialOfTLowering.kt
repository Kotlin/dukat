package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private val PARTIAL_SUFFIX = "::PARTIAL"

private fun String.withPartialSuffix() = this + PARTIAL_SUFFIX

private fun NameEntity.partialName(): NameEntity {
    val shortName = IdentifierEntity(rightMost().value + "Partial")
    return if (this is QualifierEntity) {
        QualifierEntity(left, shortName)
    } else {
        shortName
    }
}

private fun MethodSignatureDeclaration.convertToLambdaProperty(): PropertyDeclaration {
    return PropertyDeclaration(
            name = name,
            type = FunctionTypeDeclaration(
                    parameters = parameters,
                    type = type,
                    nullable = false, //TODO: I'm not sure what's nullable stands for in FunctionTypeDeclaration
                    meta = null
            ),
            optional = true,
            modifiers = modifiers,
            typeParameters = typeParameters,
            hasType = true
    )
}

private data class ClassLikeWithOwner(val classLike: ClassLikeDeclaration, val moduleOwner: ModuleDeclaration) {}

private fun ModuleDeclaration.visitClassLike(visit: (classLike: ClassLikeDeclaration, moduleOwner: ModuleDeclaration) -> Unit) {
    declarations.forEach {
        when (it) {
            is ClassDeclaration -> visit(it, this)
            is InterfaceDeclaration -> visit(it, this)
            is ModuleDeclaration -> it.visitClassLike(visit)
        }
    }
}

private fun ModuleDeclaration.collectClassLikeReferences(): Map<String, ClassLikeWithOwner> {
    val references = mutableMapOf<String, ClassLikeWithOwner>()
    visitClassLike { classLike, moduleOwner -> references[classLike.uid] = ClassLikeWithOwner(classLike, moduleOwner) }
    return references
}

private fun ClassLikeDeclaration.generatePartialInterface(
        heritageClauses: MutableSet<String>?
): GeneratedInterfaceDeclaration {
    val membersResolved = members.mapNotNull {
        when (it) {
            is PropertyDeclaration -> it.copy(optional = true)
            is MethodSignatureDeclaration -> it.convertToLambdaProperty()
            else -> null
        }
    }

    val partialParentEntities = if (heritageClauses != null) {
        parentEntities.map { heritageClauseDeclaration ->
            val partialHeritageClause = heritageClauseDeclaration.copy(
                    name = heritageClauseDeclaration.name.partialName(),
                    reference = heritageClauseDeclaration.reference?.copy(uid = heritageClauseDeclaration.reference!!.uid.withPartialSuffix())
            )

            if (partialHeritageClause.reference != null) {
                heritageClauses.add(partialHeritageClause.reference!!.uid)
            }

            partialHeritageClause
        }
    } else {
        parentEntities
    }

    return GeneratedInterfaceDeclaration(
            name.partialName(),
            membersResolved,
            typeParameters,
            partialParentEntities,
            emptyList(),
            uid.withPartialSuffix())
}

private class TypeVisitor(
        private val classLikeReferences: Map<String, ClassLikeWithOwner>,
        private val partialReferences: MutableMap<String, GeneratedInterfaceDeclaration>,
        private val heritageClauses: MutableSet<String>
) : DeclarationLowering {

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>?): TypeDeclaration {
        val singleTypeParam = declaration.params.singleOrNull()
        return if (declaration.value == IdentifierEntity("Partial") && (singleTypeParam is TypeDeclaration)) {
            val uid = singleTypeParam.reference?.uid
            val typeAny = TypeDeclaration(IdentifierEntity("Any"), emptyList())
            if (uid != null) {
                classLikeReferences[uid]?.let { (classLike, _) ->
                    val generatePartialInterface = classLike.generatePartialInterface(heritageClauses)
                    partialReferences.putIfAbsent(uid, generatePartialInterface)
                    singleTypeParam.copy(value = generatePartialInterface.name, reference = ReferenceDeclaration(generatePartialInterface.uid))
                } ?: typeAny
            } else {
                typeAny
            }
        } else {
            super.lowerTypeDeclaration(declaration, owner)
        }
    }
}

private fun ModuleDeclaration.resolveDeclarations(partialReferences: Map<String, GeneratedInterfaceDeclaration>): ModuleDeclaration {
    val declarationResolved: List<TopLevelDeclaration> = declarations.flatMap { declaration ->
        when (declaration) {
            is ClassLikeDeclaration -> {
                val uid = declaration.uid
                if (partialReferences[uid] is GeneratedInterfaceDeclaration) {
                    listOf(declaration, partialReferences[uid]!!)
                } else {
                    listOf(declaration)
                }
            }
            is ModuleDeclaration -> listOf<TopLevelDeclaration>(declaration.resolveDeclarations(partialReferences))
            else -> listOf(declaration)
        }
    }

    return copy(declarations = declarationResolved)
}


private fun ModuleDeclaration.lowerPartialOfT(): ModuleDeclaration {
    val partialReferences = mutableMapOf<String, GeneratedInterfaceDeclaration>()
    val classReferences = collectClassLikeReferences()
    val heritageClauses = LinkedHashSet<String>()

    var moduleResolved = TypeVisitor(classReferences, partialReferences, heritageClauses)
            .lowerSourceDeclaration(this, NodeOwner(this, null))

    moduleResolved = moduleResolved.resolveDeclarations(partialReferences)

    val partialReferencesFromHeritage = mutableMapOf<String, GeneratedInterfaceDeclaration>()
    val updatedClassReferences = moduleResolved.collectClassLikeReferences()
    heritageClauses.map { uid ->
        val originalUid = uid.removeSuffix(PARTIAL_SUFFIX)
        if (partialReferences[originalUid] !is ClassLikeDeclaration) {
            updatedClassReferences[originalUid]?.let { (classLike, _) ->
                partialReferencesFromHeritage.putIfAbsent(classLike.uid, classLike.generatePartialInterface(null))
            }
        }
    }
    moduleResolved = moduleResolved.resolveDeclarations(partialReferencesFromHeritage)

    return moduleResolved
}

private fun SourceFileDeclaration.lowerPartialOfT(): SourceFileDeclaration = copy(root = root.lowerPartialOfT())

private fun SourceSetDeclaration.lowerPartialOfT(): SourceSetDeclaration = copy(sources = sources.map(SourceFileDeclaration::lowerPartialOfT))

class LowerPartialOf(): TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        return source.lowerPartialOfT()
    }
}