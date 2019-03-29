package org.jetbrains.dukat.translatorString

import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.translator.ModelVisitor
import translate
import translateAnnotations
import translateAsHeritageClause
import translateHeritageNodes
import translateSignature
import translateTypeParameters

class StringTranslator : ModelVisitor {
    private var myOutput: MutableList<String> = mutableListOf()

    private fun addOutput(fragment: String) {
        myOutput.add(fragment)
    }

    fun output(): String {
        return myOutput.joinToString("\n")
    }

    override fun visitVariable(variable: VariableModel) {
        addOutput(variable.translate())
    }

    override fun visitFunction(function: FunctionModel) {
        addOutput(function.translate())
    }

    override fun visitObject(objectNode: ObjectModel) {
        val objectModel = "external object ${objectNode.name}"

        val members = objectNode.members

        val hasMembers = members.isNotEmpty()

        addOutput(objectModel + " {")

        if (hasMembers) {
            members.flatMap { it.translate() }.map({ "    " + it }).forEach {
                addOutput(it)
            }

        }

        addOutput("}")
    }

    override fun visitEnum(enumNode: EnumNode) {
        addOutput(enumNode.translate())
    }

    override fun visitInterface(interfaceModel: InterfaceModel) {
        val hasMembers = interfaceModel.members.isNotEmpty()
        val staticMembers = interfaceModel.companionObject.members

        val showCompanionObject = staticMembers.isNotEmpty() || interfaceModel.companionObject.parentEntities.isNotEmpty()

        val isBlock = hasMembers || staticMembers.isNotEmpty() || showCompanionObject
        val parents = translateHeritageNodes(interfaceModel.parentEntities)

        addOutput("${translateAnnotations(interfaceModel.annotations)}external interface ${interfaceModel.name}${translateTypeParameters(interfaceModel.typeParameters)}${parents}" + if (isBlock) " {" else "")
        if (isBlock) {
            interfaceModel.members.flatMap { it.translateSignature() }.map { "    " + it }.forEach{ addOutput(it)}

            val parents = if (interfaceModel.companionObject.parentEntities.isEmpty()) {
                ""
            } else {
                " : ${interfaceModel.companionObject.parentEntities.map { it.translateAsHeritageClause() }.joinToString(", ")}"
            }

            if (showCompanionObject) {
                addOutput("    companion object${parents} {")

                staticMembers.flatMap { it.translate() }.map({ "        ${it}" }).forEach { addOutput(it) }
                addOutput("    }")
            }

            addOutput("}")
        }

    }

    override fun visitClass(classModel: ClassModel) {
        addOutput(classModel.translate(false, 0))
    }

    override fun visitModule(moduleModel: ModuleModel) {
        if (moduleModel.declarations.isEmpty() && moduleModel.sumbodules.isEmpty()) {
            return
        }

        val containsSomethingExceptDocRoot = moduleModel.declarations.isNotEmpty()

        if (containsSomethingExceptDocRoot) {
            addOutput("${translateAnnotations(moduleModel.annotations)}package ${moduleModel.packageName}")
            addOutput("")
        }
    }

}