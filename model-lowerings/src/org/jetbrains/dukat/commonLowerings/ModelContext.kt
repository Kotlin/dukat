package org.jetbrains.dukat.commonLowerings

import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.InterfaceModel

class ModelContext {
    private val myInterfaces: MutableMap<NameEntity, InterfaceModel> = mutableMapOf()
    private val myClassNodes: MutableMap<NameEntity, ClassModel> = mutableMapOf()

    fun registerInterface(interfaceDeclaration: InterfaceModel) {
        myInterfaces[interfaceDeclaration.name] = interfaceDeclaration
    }

    fun registerClass(classDeclaration: ClassModel) {
        myClassNodes[classDeclaration.name] = classDeclaration
    }

    fun resolveInterface(name: NameEntity): InterfaceModel? = myInterfaces[name]

    fun resolveClass(name: NameEntity): ClassModel? {
        return myClassNodes[name]
    }
}