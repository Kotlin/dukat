package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.model.ClassModel
import org.jetbrains.dukat.ast.model.model.ModuleModel
import org.jetbrains.dukat.ast.model.model.SourceSetModel
import org.jetbrains.dukat.ast.model.model.transform
import org.jetbrains.dukat.ast.model.nodes.QualifiedLeftNode
import org.jetbrains.dukat.ast.model.nodes.debugTranslate
import org.jetbrains.dukat.ast.model.nodes.shiftRight
import org.jetbrains.dukat.astCommon.TopLevelDeclaration
import org.jetbrains.dukat.compiler.lowerings.model.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner


private data class ClassKey(val name: String, val moduleQualifiedName: String)

private class ClassContext : ModelWithOwnerTypeLowering {

    private val myClassMap: MutableMap<ClassKey, ClassModel> = mutableMapOf()
    private val myModuleClassesMap: MutableMap<String, MutableList<ClassModel>> = mutableMapOf()

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        myClassMap.put(ClassKey(ownerContext.node.name, ownerContext.getQualifiedName().debugTranslate()), ownerContext.node)

        val owner = ownerContext.getOwners().firstOrNull {
            (it is NodeOwner<*>) && (it.node is ModuleModel)
        } as NodeOwner<ModuleModel>

        myModuleClassesMap.getOrPut(owner.node.packageName) { mutableListOf() }.add(ownerContext.node)

        return ownerContext.node
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        myModuleClassesMap.put(moduleModel.packageName, mutableListOf())
        return super.lowerRoot(moduleModel, ownerContext)
    }

    fun resolve(name: String, qualifiedNode: QualifiedLeftNode): ClassModel? {
        val key = ClassKey(name, qualifiedNode.debugTranslate())
        return myClassMap.get(key)
    }

    fun resolveModule(moduleModel: ModuleModel, qualifiedNode: QualifiedLeftNode): ModuleModel {
        val classKey = ClassKey(moduleModel.shortName, qualifiedNode.debugTranslate())
        if (myClassMap.containsKey(classKey)) {
            val classDeclarations = mutableListOf<ClassModel>()
            val nonClassDeclarations = mutableListOf<TopLevelDeclaration>()
            moduleModel.declarations.forEach { declaration ->
                when (declaration) {
                    is ClassModel -> classDeclarations.add(declaration)
                    else -> nonClassDeclarations.add(declaration)
                }
            }

            val classModel =
                    myClassMap.get(classKey)!!


            val members= classModel.members + classDeclarations
            myClassMap.set(classKey, classModel.copy(members = members))

            return moduleModel.copy(declarations = nonClassDeclarations)
        }

        return moduleModel
    }

}


private class MergeModule(private val classContext: ClassContext) : ModelWithOwnerTypeLowering {


    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        val qualifiedName = ownerContext.getQualifiedName().shiftRight()

        if (qualifiedName != null) {
            return super.lowerRoot(classContext.resolveModule(moduleModel, qualifiedName), ownerContext)
        }

        return super.lowerRoot(moduleModel, ownerContext)
    }

}

private class IntroduceNestedClasses(private val classContext: ClassContext): ModelWithOwnerTypeLowering {

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        val resolvedClass = classContext.resolve(ownerContext.node.name, ownerContext.getQualifiedName())
        return resolvedClass ?: ownerContext.node
    }
}


fun ModuleModel.mergeNestedClasses(): ModuleModel {
    val classContext = ClassContext()
    classContext.lowerRoot(this, NodeOwner(this, null))

    val mergedModules = MergeModule(classContext).lowerRoot(this, NodeOwner(this, null))

    return IntroduceNestedClasses(classContext).lowerRoot(mergedModules, NodeOwner(mergedModules, null))
}

fun SourceSetModel.mergeNestedClasses() = transform { it.mergeNestedClasses() }