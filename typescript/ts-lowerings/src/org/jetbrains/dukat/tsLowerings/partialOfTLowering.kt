package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.astCommon.rightMost
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun NameEntity.partialName(): NameEntity {
    val shortName = IdentifierEntity(rightMost().value + "Partial")
    return if (this is QualifierEntity) {
        QualifierEntity(left, shortName)
    } else {
        shortName
    }
}

@Suppress("UNCHECKED_CAST")
private fun NodeOwner<*>.moduleOwner(): NodeOwner<ModuleDeclaration>? {
    val topOwner = generateSequence(this) {
        it.owner
    }.lastOrNull { (it.node is ModuleDeclaration) }

    return topOwner as? NodeOwner<ModuleDeclaration>
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
        typeParameters = typeParameters
    )
}

private class PartialOfTUseLowering(val references: Map<String, ClassLikeDeclaration>) : DeclarationTypeLowering {

    private var generatedInterfaces = mutableMapOf<ClassLikeDeclaration, GeneratedInterfaceDeclaration>()

    private fun ClassLikeDeclaration.generatePartialInterface(owner: NodeOwner<ModuleDeclaration>): GeneratedInterfaceDeclaration {
        val membersResolved = members.mapNotNull {
            when (it) {
                is PropertyDeclaration -> it.copy(optional = true)
                is MethodSignatureDeclaration -> it.convertToLambdaProperty()
                else -> null
            }
        }

        val parentEntitiesResolved = parentEntities.map {
            lowerHeritageClause(HeritageClauseDeclaration(
                    IdentifierEntity("Partial"),
                    @Suppress("UNCHECKED_CAST") listOf(TypeDeclaration(it.name, it.typeArguments, it.typeReference as ReferenceEntity<Declaration>?)),
                    it.extending,
                    typeReference = null
            ), owner.wrap(this))
        }

        return lowerGeneratedInterfaceDeclaration(GeneratedInterfaceDeclaration(
                name.partialName(),
                membersResolved,
                typeParameters,
                parentEntitiesResolved,
                uid + "_Partial",
                owner.node), owner)
    }

    fun generatePartial(declaration: ClassLikeDeclaration, classLikeOwner: NodeOwner<ModuleDeclaration>): GeneratedInterfaceDeclaration {
        return generatedInterfaces.getOrPut(declaration) {
            declaration.generatePartialInterface(classLikeOwner)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration, owner: NodeOwner<ClassLikeDeclaration>): HeritageClauseDeclaration {
        return if (heritageClause.name == IdentifierEntity("Partial")) {

            val type = lowerTypeDeclaration(TypeDeclaration(
                    heritageClause.name,
                    heritageClause.typeArguments,
                    heritageClause.typeReference as ReferenceEntity<Declaration>?
            ), owner.wrap(heritageClause))

            return HeritageClauseDeclaration(
                    type.value,
                    type.params,
                    heritageClause.extending,
                    type.typeReference as ReferenceEntity<ClassLikeDeclaration>?
            )
        } else {
            heritageClause
        }
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TypeDeclaration {
        val singleTypeParam = declaration.params.singleOrNull()
        return if (declaration.value == IdentifierEntity("Partial") && (singleTypeParam is TypeDeclaration)) {
            val typeReference = singleTypeParam.typeReference

            when {
                typeReference == null -> declaration
                references[typeReference.uid] == null -> declaration
                else -> {
                    val decl = generatePartial(references[typeReference.uid]!!, owner.moduleOwner()!!)

                    TypeDeclaration(
                            decl.name,
                            singleTypeParam.params,
                            ReferenceEntity(decl.uid),
                            singleTypeParam.nullable || declaration.nullable)
                }
            }
        } else {
            declaration
        }
    }

    override fun lowerDocumentRoot(documentRoot: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>): ModuleDeclaration {
        val lowerDocumentRoot = super.lowerDocumentRoot(documentRoot, owner)
        return lowerDocumentRoot.copy(declarations = lowerDocumentRoot.declarations + generatedInterfaces.values)
    }
}

private fun ModuleDeclaration.lowerPartialOfT(): ModuleDeclaration {
    return PartialOfTUseLowering(collectReferences())
            .lowerDocumentRoot(this, NodeOwner(this, null))
}

private fun SourceFileDeclaration.lowerPartialOfT(): SourceFileDeclaration = copy(root = root.lowerPartialOfT())

fun SourceSetDeclaration.lowerPartialOfT(): SourceSetDeclaration = copy(sources = sources.map(SourceFileDeclaration::lowerPartialOfT))