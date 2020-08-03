package org.jetbrains.dukat.tsLowerings

import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.ownerContext.wrap
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.types.KeyOfTypeDeclaration
import org.jetbrains.dukat.tsmodel.MemberDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.NamedMemberDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration

private class KeyOfAndLookupLowering(private val context: KeyOfAndLookupContext) : DeclarationLowering {
    override fun lowerKeyOfTypeDeclaration(
        declaration: KeyOfTypeDeclaration,
        owner: NodeOwner<ParameterOwnerDeclaration>?
    ): ParameterValueDeclaration {
        val type = super.lowerParameterValue(declaration.type, owner.wrap(declaration))
        if (type is TypeDeclaration) {
            val reference = type.reference
            if (reference != null) {
                val namedMembers = context.getNamedMembers(reference.uid)
                return UnionTypeDeclaration(
                    namedMembers.map { StringLiteralDeclaration(it.name) }
                )
            }
        }
        return declaration.copy(
            type = type
        )
    }

    private fun findPropertyTypeByString(properties: List<PropertyDeclaration>, value: String): ParameterValueDeclaration? {
        return properties.find { it.name == value }?.type
    }

    override fun lowerIndexTypeDeclaration(
        declaration: IndexTypeDeclaration,
        owner: NodeOwner<ParameterOwnerDeclaration>?
    ): ParameterValueDeclaration {
        val objectType = super.lowerParameterValue(declaration.objectType, owner.wrap(declaration))
        val indexType = super.lowerParameterValue(declaration.indexType, owner.wrap(declaration))
        if (objectType is TypeDeclaration) {
            val reference = objectType.reference
            if (reference != null) {
                val properties = context.getProperties(reference.uid)
                val newType = when (indexType) {
                    is StringLiteralDeclaration -> findPropertyTypeByString(properties, indexType.token)
                    is UnionTypeDeclaration -> indexType.copy(
                        params = indexType.params.filterIsInstance<StringLiteralDeclaration>().mapNotNull { unionMember ->
                            findPropertyTypeByString(properties, unionMember.token)
                        }
                    )
                    else -> null
                }
                if (newType != null) {
                    return newType
                }
            }
        }
        return declaration.copy(
            objectType = objectType,
            indexType = indexType
        )
    }
}

private class KeyOfAndLookupContext : DeclarationLowering {

    private val registeredMembers: MutableMap<String, List<MemberDeclaration>> = mutableMapOf()

    override fun lowerClassLikeDeclaration(
        declaration: ClassLikeDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        registeredMembers[declaration.uid] = declaration.members
        return super.lowerClassLikeDeclaration(declaration, owner)
    }

    fun getProperties(uid: String): List<PropertyDeclaration> {
        return registeredMembers[uid]?.filterIsInstance<PropertyDeclaration>() ?: emptyList()
    }

    fun getNamedMembers(uid: String): List<NamedMemberDeclaration> {
        return registeredMembers[uid]?.filterIsInstance<NamedMemberDeclaration>() ?: emptyList()
    }
}

class ConvertKeyOfsAndLookups : TsLowering {
    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        val context = KeyOfAndLookupContext()
        source.sources.map {
            context.lowerSourceDeclaration(it.root)
        }

        return source.copy(sources = source.sources.map { sourceFileDeclaration ->
            sourceFileDeclaration.copy(root = KeyOfAndLookupLowering(
                context
            ).lowerSourceDeclaration(sourceFileDeclaration.root))
        })
    }
}