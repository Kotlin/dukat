import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsLowerings.DeclarationTypeLowering
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.ParameterOwnerDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration


private fun TypeDeclaration.resolveAsSubstitution(): TypeDeclaration {
    if (value == IdentifierEntity("ReadonlyArray")) {
        if (typeReference?.uid?.startsWith("lib-") == true) {
            return copy(value = IdentifierEntity("Array"))
        }
    }

    return this
}

private class SubstituteTsEntity : DeclarationTypeLowering {
    override fun lowerTypeDeclaration(declaration: TypeDeclaration, owner: NodeOwner<ParameterOwnerDeclaration>): TypeDeclaration {
        return super.lowerTypeDeclaration(declaration.resolveAsSubstitution(), owner)
    }
}

private fun ModuleDeclaration.substituteTsStdLibEntities(): ModuleDeclaration {
    return SubstituteTsEntity().lowerDocumentRoot(this)
}

private fun SourceFileDeclaration.substituteTsStdLibEntities() = copy(root = root.substituteTsStdLibEntities())

fun SourceSetDeclaration.substituteTsStdLibEntities() = copy(sources = sources.map(SourceFileDeclaration::substituteTsStdLibEntities))
