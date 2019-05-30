package org.jetbrains.dukat.compiler.lowerings.merge

import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.processing.shiftRight
import org.jetbrains.dukat.ast.model.nodes.processing.translate
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.transform
import org.jetbrains.dukat.compiler.lowerings.model.ModelWithOwnerTypeLowering
import org.jetbrains.dukat.ownerContext.NodeOwner
import javax.naming.Name


private data class ClassKey(val name: NameEntity, val moduleQualifiedName: String)

private class ClassContext : ModelWithOwnerTypeLowering {

    private val myClassMap: MutableMap<ClassKey, ClassModel> = mutableMapOf()
    private val myModuleClassesMap: MutableMap<NameEntity, MutableList<ClassModel>> = mutableMapOf()

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>): ClassModel {
        myClassMap[ClassKey(IdentifierEntity(ownerContext.node.name), ownerContext.getQualifiedName().translate())] = ownerContext.node

        val owner = ownerContext.getOwners().firstOrNull {
            (it is NodeOwner<*>) && (it.node is ModuleModel)
        } as NodeOwner<ModuleModel>

        myModuleClassesMap.getOrPut(owner.node.packageName) { mutableListOf() }.add(ownerContext.node)

        return ownerContext.node
    }

    override fun lowerRoot(moduleModel: ModuleModel, ownerContext: NodeOwner<ModuleModel>): ModuleModel {
        myModuleClassesMap[moduleModel.packageName] = mutableListOf()
        return super.lowerRoot(moduleModel, ownerContext)
    }

    fun resolve(name: String, qualifiedNode: NameEntity): ClassModel? {
        val key = ClassKey(IdentifierEntity(name), qualifiedNode.translate())
        return myClassMap.get(key)
    }

    fun resolveModule(moduleModel: ModuleModel, qualifiedNode: NameEntity): ModuleModel {
        val classKey = ClassKey(moduleModel.shortName, qualifiedNode.translate())
        if (myClassMap.containsKey(classKey)) {
            val classDeclarations = mutableListOf<ClassModel>()
            val nonClassDeclarations = mutableListOf<TopLevelNode>()
            moduleModel.declarations.forEach { declaration ->
                when (declaration) {
                    is ClassModel -> classDeclarations.add(declaration)
                    else -> nonClassDeclarations.add(declaration)
                }
            }

            val classModel =
                    myClassMap.get(classKey)!!


            val members = classModel.members + classDeclarations
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

private class IntroduceNestedClasses(private val classContext: ClassContext) : ModelWithOwnerTypeLowering {

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