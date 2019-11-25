package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel

class ModelContext {
    private val myInterfaces: MutableMap<NameEntity, InterfaceModel> = mutableMapOf()
    private val myClassNodes: MutableMap<NameEntity, ClassModel> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceModel, owner: ModuleModel) {
        val name = owner.name.appendLeft(interfaceDeclaration.name)
        myInterfaces[name] = interfaceDeclaration
    }

    fun registerClass(classDeclaration: ClassModel, owner: ModuleModel) {
        val name = owner.name.appendLeft(classDeclaration.name)
        myClassNodes[name] = classDeclaration
    }

    fun resolveInterface(name: NameEntity?): InterfaceModel? = myInterfaces[name]

    fun resolveClass(name: NameEntity?): ClassModel? {
        return myClassNodes[name]
    }
}