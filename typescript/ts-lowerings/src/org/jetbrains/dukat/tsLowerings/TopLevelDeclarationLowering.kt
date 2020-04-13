import org.jetbrains.dukat.ownerContext.NodeOwner
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ClassLikeDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.FunctionOwnerDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.TopLevelDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration

interface TopLevelDeclarationLowering {
    fun lowerVariableDeclaration(declaration: VariableDeclaration, owner: NodeOwner<ModuleDeclaration>?): VariableDeclaration = declaration
    fun lowerFunctionDeclaration(declaration: FunctionDeclaration, owner: NodeOwner<FunctionOwnerDeclaration>?): FunctionDeclaration = declaration
    fun lowerClassDeclaration(declaration: ClassDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? = declaration
    fun lowerInterfaceDeclaration(declaration: InterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? = declaration
    fun lowerGeneratedInterfaceDeclaration(declaration: GeneratedInterfaceDeclaration, owner: NodeOwner<ModuleDeclaration>?): GeneratedInterfaceDeclaration = declaration
    fun lowerTypeAliasDeclaration(declaration: TypeAliasDeclaration, owner: NodeOwner<ModuleDeclaration>?): TypeAliasDeclaration = declaration

    fun lowerClassLikeDeclaration(declaration: ClassLikeDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        return when (declaration) {
            is InterfaceDeclaration -> lowerInterfaceDeclaration(declaration, owner)
            is ClassDeclaration -> lowerClassDeclaration(declaration, owner)
            is GeneratedInterfaceDeclaration -> lowerGeneratedInterfaceDeclaration(declaration, owner)
            else -> declaration
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerTopLevelDeclaration(declaration: TopLevelDeclaration, owner: NodeOwner<ModuleDeclaration>?): TopLevelDeclaration? {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration, owner)
            is FunctionDeclaration -> lowerFunctionDeclaration(declaration, owner as NodeOwner<FunctionOwnerDeclaration>)
            is ClassLikeDeclaration -> lowerClassLikeDeclaration(declaration, owner)
            is ModuleDeclaration -> lowerModuleModel(declaration, owner)
            is TypeAliasDeclaration -> lowerTypeAliasDeclaration(declaration, owner)
            else -> declaration
        }
    }

    fun lowerModuleModel(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>?): ModuleDeclaration? {
        return moduleDeclaration.copy(declarations = moduleDeclaration.declarations.mapNotNull { declaration ->
            lowerTopLevelDeclaration(declaration, owner)
        })
    }

    fun lowerSourceDeclaration(moduleDeclaration: ModuleDeclaration, owner: NodeOwner<ModuleDeclaration>? = NodeOwner(moduleDeclaration, null)): ModuleDeclaration {
        return moduleDeclaration.copy(declarations = moduleDeclaration.declarations.mapNotNull { declaration ->
            lowerTopLevelDeclaration(declaration, owner)
        })
    }

}