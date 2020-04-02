import org.jetbrains.dukat.tsmodel.DefinitionInfoDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.WithUidDeclaration

interface MergeableDeclaration : TopLevelDeclaration, WithUidDeclaration {
    val definitionsInfo: List<DefinitionInfoDeclaration>
}