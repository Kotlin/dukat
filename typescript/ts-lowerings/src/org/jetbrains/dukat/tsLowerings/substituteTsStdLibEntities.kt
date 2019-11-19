import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.ReferenceEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsLowerings.DeclarationTypeLowering
import org.jetbrains.dukat.tsLowerings.getUID
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.Declaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration

private fun TypeDeclaration.isLibReference(): Boolean {
    return typeReference.isLibReference()
}

private fun HeritageClauseDeclaration.isLibReference(): Boolean {
    return typeReference.isLibReference()
}

private fun ReferenceEntity<*>?.isLibReference(): Boolean {
    return this?.uid?.startsWith("lib-") == true
}

private fun TypeDeclaration.resolveAsSubstitution(): TypeDeclaration {
    if (value == IdentifierEntity("ReadonlyArray")) {
        if (isLibReference()) {
            return copy(value = IdentifierEntity("Array"))
        }
    }

    return this
}

@Suppress("UNCHECKED_CAST")
private fun ClassLikeDeclaration.convertToTypeAlias(parentEntity: HeritageClauseDeclaration): TypeAliasDeclaration {
    return TypeAliasDeclaration(
            aliasName = name,
            typeParameters = typeParameters.map { it.name } ,
            uid = uid,
            typeReference = TypeDeclaration(
                    value = parentEntity.name,
                    params = parentEntity.typeArguments,
                    typeReference = parentEntity.typeReference as ReferenceEntity<Declaration>,
                    nullable = false,
                    meta = null
            )
    )
}

private class SubstituteLowering : DeclarationTypeLowering {

    private val stdLibFinalEntities = setOf<NameEntity>(
            IdentifierEntity("Array"),
            IdentifierEntity("Error")
    )

    override fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>): TopLevelDeclaration {
        val declarationResolved: TopLevelDeclaration = if (declaration is ClassLikeDeclaration) {
            val forbiddenParent = declaration.parentEntities.firstOrNull { parentEntity ->
                parentEntity.isLibReference() and stdLibFinalEntities.contains(parentEntity.name)
            }
            if (forbiddenParent == null) {
                null
            } else {
                declaration.convertToTypeAlias(forbiddenParent)
            }
        } else {
            null
        } ?: declaration

        return super.lowerTopLevelDeclaration(declarationResolved, owner)
    }

    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration.resolveAsSubstitution(), owner)
    }
}

private class TopLevelVisitor(private val visitor: (TopLevelDeclaration) -> Unit) : DeclarationTypeLowering {

    override fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>): TopLevelDeclaration {
        visitor.invoke(declaration)
        return super.lowerTopLevelDeclaration(declaration, owner)
    }

    fun visit(module: ModuleDeclaration) {
        lowerDocumentRoot(module)
    }
}


private fun ModuleDeclaration.substituteTsStdLibEntities(): ModuleDeclaration {
    val moduleResolved = SubstituteLowering().lowerDocumentRoot(this)

    val m = mutableMapOf<String, NameEntity>()
    TopLevelVisitor {
        if (it is ClassLikeDeclaration) {
            m[it.uid] = it.name
        }
    }.visit(moduleResolved)

    return moduleResolved
}

private fun SourceFileDeclaration.substituteTsStdLibEntities(): SourceFileDeclaration {
    return copy(root = root.substituteTsStdLibEntities())
}

fun SourceSetDeclaration.substituteTsStdLibEntities(): SourceSetDeclaration {
    return copy(sources = sources.map(SourceFileDeclaration::substituteTsStdLibEntities))
}
