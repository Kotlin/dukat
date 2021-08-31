import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.toposort.toposort
import org.jetbrains.dukat.tsLowerings.ResolveTypeParamsInConstructor
import org.jetbrains.dukat.tsLowerings.TopLevelDeclarationResolver
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration

class IntroduceMissingConstructors : TopLevelDeclarationLowering {
    private lateinit var declarationResolver: TopLevelDeclarationResolver
    private val parentConstructorsMap = mutableMapOf<String, ClassWithConstructors>()

    private data class ClassWithConstructors(
        val classDeclaration: ClassDeclaration,
        val constructors: List<ConstructorDeclaration>
    )

    private fun ClassWithConstructors?.addConstructorsInto(declaration: ClassDeclaration): ClassDeclaration {
        if (this == null || constructors.isEmpty() || declaration.parentEntities.isEmpty()) {
            return declaration
        }
        // TODO: this is a strange thing to do and I need to figure out how to avoid this
        return declaration.copy(members = (constructors + declaration.members).distinct())
    }

    private fun ClassWithConstructors.getConstructorsWithResolvedTypeParams(actualTypes: List<ParameterValueDeclaration>) =
        when {
            classDeclaration.typeParameters.isEmpty() -> constructors
            else -> {
                val typesMapping = classDeclaration.typeParameters.withIndex().map { (index, type) ->
                    val resolvedType = actualTypes.getOrNull(index) ?: type.defaultValue
                    type.name to resolvedType!!
                }.toMap()
                val resolver = ResolveTypeParamsInConstructor(typesMapping)
                constructors.map { resolver.lowerConstructorDeclaration(it, null) }
            }
        }


    override fun lowerClassDeclaration(
        declaration: ClassDeclaration,
        owner: NodeOwner<ModuleDeclaration>?
    ): TopLevelDeclaration? {
        val classWithConstructors = parentConstructorsMap[declaration.uid]
        val declarationResolved = classWithConstructors.addConstructorsInto(declaration)
        return super.lowerClassDeclaration(declarationResolved, owner)
    }

    override fun lower(source: SourceSetDeclaration): SourceSetDeclaration {
        declarationResolver = TopLevelDeclarationResolver(source)

        val classDeclarations =
            declarationResolver.asIterable().filterIsInstance<ClassDeclaration>().toposort { classDeclaration ->
                classDeclaration.parentEntities.mapNotNull { declarationResolver.resolveRecursive(it.typeReference?.uid) as? ClassDeclaration }
            }

        classDeclarations.forEach { classDeclaration ->
            val constructors = classDeclaration.members.filterIsInstance<ConstructorDeclaration>()
            val constructorsResolved = when {
                constructors.isNotEmpty() -> constructors
                else -> classDeclaration.parentEntities.flatMap { parentEntity ->
                    val uid = parentEntity.typeReference?.uid
                    val parentConstructors = uid?.let { parentConstructorsMap[it] }
                    parentConstructors?.getConstructorsWithResolvedTypeParams(parentEntity.typeArguments) ?: emptyList()
                }
            }

            parentConstructorsMap[classDeclaration.uid] = ClassWithConstructors(classDeclaration, constructorsResolved)
        }


        return super.lower(source)
    }
}