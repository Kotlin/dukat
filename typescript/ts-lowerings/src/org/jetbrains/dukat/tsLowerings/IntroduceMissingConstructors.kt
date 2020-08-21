import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.toposort.toposort
import org.jetbrains.dukat.tsLowerings.TopLevelDeclarationResolver
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration

class IntroduceMissingConstructors : TopLevelDeclarationLowering {
    private lateinit var declarationResolver: TopLevelDeclarationResolver
    private val parentConstructorsMap = mutableMapOf<String, List<ConstructorDeclaration>>()

    override fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        val constructors = parentConstructorsMap[declaration.uid]
        val declarationResolved = if (constructors.isNullOrEmpty()) {
            declaration
        } else {
            declaration.copy(members = constructors + declaration.members)
        }
        return super.lowerClassDeclaration(declarationResolved, owner)
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        declarationResolver = TopLevelDeclarationResolver(source)

        val classDeclarations = declarationResolver.asIterable().filterIsInstance(ClassDeclaration::class.java).toposort { classDeclaration ->
            classDeclaration.parentEntities.mapNotNull { declarationResolver.resolveRecursive(it.typeReference?.uid) as? ClassDeclaration }
        }

        classDeclarations.forEach { classDeclaration ->
            val constructors = classDeclaration.members.filterIsInstance(ConstructorDeclaration::class.java)
            val constructorsResolved = if (constructors.isEmpty()) {
                classDeclaration.parentEntities.flatMap { parentEntity ->
                    parentEntity.typeReference?.uid?.let { parentConstructorsMap[it] } ?: emptyList()
                }
            } else {
                constructors
            }

            parentConstructorsMap[classDeclaration.uid] = constructorsResolved
        }


        return super.lower(source)
    }
}