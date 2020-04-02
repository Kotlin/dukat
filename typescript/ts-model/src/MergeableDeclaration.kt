import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

interface MergeableDeclaration : TopLevelDeclaration {
    val definitionsInfo: List<DefinitionInfoDeclaration>
}