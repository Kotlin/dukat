package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.astCommon.*
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.*
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private class PartialOfTUseLowering(val references: Map<String, ClassLikeDeclaration>) : DeclarationTypeLowering {

    var newInterfaces = mutableMapOf<Declaration, GeneratedInterfaceDeclaration>()




    fun generatePartial(declaration: Declaration, owner: NodeOwner<ModuleDeclaration>): GeneratedInterfaceDeclaration {
        return newInterfaces.getOrPut(declaration) {

            declaration as ClassLikeDeclaration
            val members = when(declaration) {
                is ClassDeclaration -> declaration.members
                is InterfaceDeclaration -> declaration.members
                is GeneratedInterfaceDeclaration -> declaration.members
                else -> error("Unknown declaration type: $declaration")
            }

            val heritage = when(declaration) {
                is ClassDeclaration -> declaration.parentEntities
                is InterfaceDeclaration -> declaration.parentEntities
                is GeneratedInterfaceDeclaration -> declaration.parentEntities
                else -> error("Unknown declaration type: $declaration")
            }



            val newName = partialName(declaration.name)

            val newMembers = members.mapNotNull {
                when (it) {
                    is PropertyDeclaration -> it.copy(type = it.type, optional = true)
                    else -> null
                }
            }

            val newHeritage = heritage.map {
                lowerHeritageClause(HeritageClauseDeclaration(
                    IdentifierEntity("Partial"),
                    @Suppress("UNCHECKED_CAST") listOf(TypeDeclaration(it.name, it.typeArguments, it.typeReference as ReferenceEntity<Declaration>?)),
                    it.extending,
                    typeReference = null
                ))
            }

            lowerGeneratedInterfaceDeclaration(GeneratedInterfaceDeclaration(
                newName, newMembers, declaration.typeParameters, newHeritage, declaration.getUID() + "_Partial", owner.node
            ), owner)


        }
    }

    fun partialName(tName: NameEntity): NameEntity {

        val shortName = tName.rightMost() as IdentifierEntity
        val newShortName = IdentifierEntity(shortName.value + "Partial")
        return if (tName is QualifierEntity) {
            QualifierEntity(tName.left, newShortName)
        } else {
            newShortName
        }
    }

    lateinit var owner: NodeOwner<ModuleDeclaration>

    override fun lowerTopLevelDeclaration(
        declaration: TopLevelEntity,
        owner: NodeOwner<ModuleDeclaration>
    ): TopLevelEntity {
        this.owner = owner
        return super.lowerTopLevelDeclaration(declaration, owner)
    }



    override fun lowerHeritageClause(heritageClause: HeritageClauseDeclaration): HeritageClauseDeclaration {
        val prev = super.lowerHeritageClause(heritageClause)
        if (prev.name != IdentifierEntity("Partial")) return prev
        @Suppress("UNCHECKED_CAST")
        val type = TypeDeclaration(
            prev.name,
            prev.typeArguments,
            prev.typeReference as ReferenceEntity<Declaration>?
        )

        val lowerType = lowerTypeDeclaration(type)
        if (lowerType === type) return prev
        @Suppress("UNCHECKED_CAST")
        return HeritageClauseDeclaration(
            lowerType.value,
            lowerType.params,
            prev.extending,
            lowerType.typeReference as ReferenceEntity<ClassLikeDeclaration>?
        )
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration): TypeDeclaration {
        val prev = super.lowerTypeDeclaration(declaration)
        val singleTypeParam = prev.params.singleOrNull() as? TypeDeclaration
        if (prev.value == IdentifierEntity("Partial") && singleTypeParam != null) {

            val ref = singleTypeParam.typeReference ?: return prev
            val decl = generatePartial(references[ref.uid] ?: error("could not find by uid: ${ref.uid}, type = $prev"), owner)

            return TypeDeclaration(
                decl.name,
                singleTypeParam.params,
                ReferenceEntity(decl.uid),
                singleTypeParam.nullable || prev.nullable)
        }
        return prev
    }

}



fun ModuleDeclaration.lowerPartialOfT(): ModuleDeclaration {
    val refs = collectReferences(mutableMapOf())
    val lowering = PartialOfTUseLowering(refs)
    return lowering.lowerDocumentRoot(this, NodeOwner(this, null)).let {
        it.copy(declarations = it.declarations + lowering.newInterfaces.values)
    }
}

fun SourceFileDeclaration.lowerPartialOfT(): SourceFileDeclaration = copy(root = root.lowerPartialOfT())

fun SourceSetDeclaration.lowerPartialOfT(): SourceSetDeclaration = copy(sources = sources.map(SourceFileDeclaration::lowerPartialOfT))