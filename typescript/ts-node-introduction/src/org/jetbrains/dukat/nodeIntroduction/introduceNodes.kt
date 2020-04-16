package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.TopLevelNode
import org.jetbrains.dukat.ast.model.TypeParameterNode
import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.ExportAssignmentNode
import org.jetbrains.dukat.ast.model.nodes.ExportableNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNode
import org.jetbrains.dukat.ast.model.nodes.FunctionNodeContextIrrelevant
import org.jetbrains.dukat.ast.model.nodes.FunctionTypeNode
import org.jetbrains.dukat.ast.model.nodes.GeneratedInterfaceReferenceNode
import org.jetbrains.dukat.ast.model.nodes.HeritageNode
import org.jetbrains.dukat.ast.model.nodes.ImportNode
import org.jetbrains.dukat.ast.model.nodes.InterfaceNode
import org.jetbrains.dukat.ast.model.nodes.MemberNode
import org.jetbrains.dukat.ast.model.nodes.MethodNode
import org.jetbrains.dukat.ast.model.nodes.ModuleNode
import org.jetbrains.dukat.ast.model.nodes.ObjectNode
import org.jetbrains.dukat.ast.model.nodes.ParameterNode
import org.jetbrains.dukat.ast.model.nodes.PropertyNode
import org.jetbrains.dukat.ast.model.nodes.ReferenceNode
import org.jetbrains.dukat.ast.model.nodes.ReferenceOriginNode
import org.jetbrains.dukat.ast.model.nodes.SourceFileNode
import org.jetbrains.dukat.ast.model.nodes.SourceSetNode
import org.jetbrains.dukat.ast.model.nodes.StringLiteralUnionNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.constants.SELF_REFERENCE_TYPE
import org.jetbrains.dukat.ast.model.nodes.export.JsDefault
import org.jetbrains.dukat.ast.model.nodes.metadata.IntersectionMetadata
import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.Lowering
import org.jetbrains.dukat.astCommon.MemberEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.QualifierEntity
import org.jetbrains.dukat.astCommon.TopLevelEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.process
import org.jetbrains.dukat.astCommon.unquote
import org.jetbrains.dukat.moduleNameResolver.ModuleNameResolver
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.stdlib.TSLIBROOT
import org.jetbrains.dukat.tsmodel.CallSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ClassDeclaration
import org.jetbrains.dukat.tsmodel.ConstructorDeclaration
import org.jetbrains.dukat.tsmodel.EnumDeclaration
import org.jetbrains.dukat.tsmodel.FunctionDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceDeclaration
import org.jetbrains.dukat.tsmodel.GeneratedInterfaceReferenceDeclaration
import org.jetbrains.dukat.tsmodel.HeritageClauseDeclaration
import org.jetbrains.dukat.tsmodel.ImportEqualsDeclaration
import org.jetbrains.dukat.tsmodel.InterfaceDeclaration
import org.jetbrains.dukat.tsmodel.MethodSignatureDeclaration
import org.jetbrains.dukat.tsmodel.ModifierDeclaration
import org.jetbrains.dukat.tsmodel.ModuleDeclaration
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.ObjectLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.ParameterValueDeclaration
import org.jetbrains.dukat.tsmodel.types.StringLiteralDeclaration
import org.jetbrains.dukat.tsmodel.types.TupleDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeDeclaration
import org.jetbrains.dukat.tsmodel.types.TypeParamReferenceDeclaration
import org.jetbrains.dukat.tsmodel.types.UnionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.canBeJson


private fun unquote(name: String): String {
    return name.replace("(?:^[\"|\'`])|(?:[\"|\'`]$)".toRegex(), "")
}

private fun TypeDeclaration.isPrimitive(primitive: String): Boolean {
    return when (this.value) {
        is IdentifierEntity -> (value as IdentifierEntity).value == primitive
        else -> false
    }
}

private enum class PARAMETER_CONTEXT {
    IRRELEVANT,
    FUNCTION_TYPE
}

private fun ParameterValueDeclaration.extractVarargType(): ParameterValueDeclaration {
    if (this is TypeDeclaration) {
        when {
            isPrimitive("Array") -> return params[0]
            isPrimitive("Any") -> return this
        }
    }

    return this
}

private fun ParameterDeclaration.convertToNode(context: PARAMETER_CONTEXT = PARAMETER_CONTEXT.IRRELEVANT): ParameterNode = ParameterNode(
        name = name,
        type = (if (vararg && context == PARAMETER_CONTEXT.IRRELEVANT) {
            type.extractVarargType()
        } else {
            type
        }).convertToNode(),
        initializer = if (initializer != null || optional) {
            TypeValueNode(IdentifierEntity("definedExternally"), emptyList())
        } else null,
        meta = null,
        vararg = vararg,
        optional = optional
)

private fun UnionTypeDeclaration.canBeTranslatedAsStringLiteral(): Boolean {
    return params.all { it is StringLiteralDeclaration }
}

private fun ParameterValueDeclaration.convertToNode(meta: ParameterValueDeclaration? = null): ParameterValueDeclaration {
    val declaration = this
    return when (declaration) {
        is TypeParamReferenceDeclaration -> TypeParameterNode(
                name = declaration.value,
                nullable = declaration.nullable,
                meta = meta ?: declaration.meta
        )
        is TypeDeclaration -> TypeValueNode(
                value = declaration.value,
                params = declaration.params.map { param -> param.convertToNode() },
                typeReference = declaration.reference?.let {
                    ReferenceNode(it.uid)
                },
                nullable = declaration.nullable,
                meta = meta ?: declaration.meta
        )
        //TODO: investigate where we still have FunctionTypeDeclarations up to this point
        is FunctionTypeDeclaration -> FunctionTypeNode(
                parameters = declaration.parameters.map { parameterDeclaration ->
                    parameterDeclaration.convertToNode(PARAMETER_CONTEXT.FUNCTION_TYPE)
                },
                type = declaration.type.convertToNode(),
                nullable = declaration.nullable,
                meta = meta ?: declaration.meta
        )
        is GeneratedInterfaceReferenceDeclaration -> GeneratedInterfaceReferenceNode(
                declaration.name,
                declaration.typeParameters,
                declaration.reference,
                declaration.nullable,
                meta ?: declaration.meta
        )
        is IntersectionTypeDeclaration -> {
            declaration.params[0].convertToNode(IntersectionMetadata(declaration.params.map { it.convertToNode() }))
        }
        is StringLiteralDeclaration -> {
            StringLiteralUnionNode(
                    params = listOf(declaration.token),
                    nullable = declaration.nullable
            )
        }
        is UnionTypeDeclaration ->
            if (declaration.canBeTranslatedAsStringLiteral()) {
                StringLiteralUnionNode(
                        params = declaration.params.mapNotNull { (it as? StringLiteralDeclaration)?.token },
                        nullable = declaration.nullable
                )
            } else {
                UnionTypeNode(
                        params = declaration.params.map { param -> param.convertToNode() },
                        nullable = declaration.nullable,
                        meta = meta ?: declaration.meta
                )
            }
        is TupleDeclaration -> TupleTypeNode(
                params = declaration.params.map { param -> param.convertToNode() },
                nullable = declaration.nullable,
                meta = meta ?: declaration.meta
        )
        is ThisTypeDeclaration -> SELF_REFERENCE_TYPE
        else -> declaration
    }.lowerAsNullable()
}

private fun ParameterValueDeclaration.resolveAsNullableType(): ParameterValueDeclaration? {
    return when (this) {
        is UnionTypeNode -> {
            val paramsFiltered = params.filter { param ->
                when (param) {
                    is TypeValueNode -> {
                        val value = param.value
                        value != IdentifierEntity("undefined") && value != IdentifierEntity("null")
                    }
                    else -> true
                }
            }

            if ((paramsFiltered.size < params.size)) {
                if (paramsFiltered.size == 1) {
                    paramsFiltered[0]
                } else {
                    copy(params = paramsFiltered, nullable = true)
                }
            } else {
                null
            }
        }
        else -> null
    }
}

private fun ParameterValueDeclaration.lowerAsNullable(): ParameterValueDeclaration {
    return resolveAsNullableType()?.let { nullableType ->
        when (nullableType) {
            is TypeValueNode -> nullableType.copy(nullable = true, meta = null)
            is TypeParameterNode -> nullableType.copy(nullable = true)
            is FunctionTypeNode -> nullableType.copy(nullable = true, meta = null)
            is UnionTypeNode -> nullableType
            is IntersectionTypeDeclaration -> nullableType
            else -> raiseConcern("can not lower nullables for unknown param type ${nullableType}") {
                nullableType
            }
        }
    } ?: this
}

private class LowerDeclarationsToNodes(
        private val fileName: String,
        private val moduleNameResolver: ModuleNameResolver
) {

    private fun FunctionDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    private fun PropertyDeclaration.isStatic() = modifiers.contains(ModifierDeclaration.STATIC_KEYWORD)

    fun convertPropertyDeclaration(declaration: PropertyDeclaration): PropertyNode {
        return PropertyNode(
                declaration.name,
                (if (declaration.optional) declaration.type.makeNullable() else declaration.type).convertToNode(),
                convertTypeParameters(declaration.typeParameters),

                declaration.isStatic(),
                declaration.optional,
                declaration.optional,  // TODO: it's actually wrong

                true
        )
    }

    private fun convertParameters(parameters: List<ParameterDeclaration>): List<ParameterNode> {
        return parameters.map { param -> param.convertToNode() }
    }

    private fun convertTypeParameters(typeParams: List<TypeParameterDeclaration>): List<TypeValueNode> {
        return typeParams.map { typeParam ->
            TypeValueNode(
                    value = typeParam.name,
                    params = typeParam.constraints.map { it.convertToNode() }
            )
        }
    }

    private fun convertMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberNode {
        return if (declaration.optional) {
            PropertyNode(
                    declaration.name,
                    FunctionTypeNode(
                            convertParameters(declaration.parameters),
                            declaration.type.convertToNode(),
                            true,
                            null
                    ),
                    convertTypeParameters(declaration.typeParameters),
                    false,
                    true,
                    false,
                    true
            )
        } else {
            MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type.convertToNode(),
                    convertTypeParameters(declaration.typeParameters),
                    false,
                    false,
                    true,
                    null,
                    null
            )
        }
    }


    private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): List<MethodNode> {
        return listOf(
                MethodNode(
                        "get",
                        convertParameters(declaration.indexTypes),
                        declaration.returnType.makeNullable().convertToNode(),
                        emptyList(),
                        false,
                        true,
                        true,
                        null,
                        null
                ),
                MethodNode(
                        "set",
                        convertParameters(declaration.indexTypes.toMutableList() + listOf(ParameterDeclaration("value", declaration.returnType.convertToNode(), null, false, false))),
                        TypeValueNode(IdentifierEntity("Unit"), emptyList()),
                        emptyList(),
                        false,
                        true,
                        true,
                        null,
                        null
                )
        )
    }


    private fun CallSignatureDeclaration.convert(): MethodNode {
        return MethodNode(
                "invoke",
                convertParameters(parameters),
                type.convertToNode(),
                convertTypeParameters(typeParameters),
                false,
                true,
                true,
                null,
                null
        )
    }

    private fun convertToHeritageNodes(declarations: List<HeritageClauseDeclaration>): List<HeritageNode> {
        return declarations.map { declaration ->
            HeritageNode(
                    name = declaration.name.convert(),
                    typeArguments = declaration.typeArguments.map { it.convertToNode() },
                    reference = declaration.reference?.let {
                        val origin = when (it.origin) {
                            ReferenceOriginDeclaration.IMPORT -> ReferenceOriginNode.IMPORT
                            ReferenceOriginDeclaration.NAMED_IMPORT -> ReferenceOriginNode.NAMED_IMPORT
                            else -> ReferenceOriginNode.IRRELEVANT
                        }
                        ReferenceNode(it.uid, origin)
                    }
            )
        }
    }

    private fun ClassDeclaration.convert(inDeclaredModule: Boolean): ClassNode {

        val exportQualifier = if (hasDefaultModifier() && hasExportModifier()) {
            JsDefault()
        } else null

        val declaration = ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters.map { typeParameter ->
                    TypeValueNode(typeParameter.name, typeParameter.constraints.map { it.convertToNode() })
                },
                convertToHeritageNodes(parentEntities),
                null,

                uid,
                exportQualifier,
                inDeclaredModule || hasDeclareModifier()
        )

        return declaration
    }

    private fun InterfaceDeclaration.convert(): List<TopLevelNode> {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                null,
                false,
                uid,
                true
        )

        return listOf(declaration)
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                null,
                true,
                uid,
                true
        )

        return declaration
    }


    private fun ConstructorDeclaration.convert(): ConstructorNode {
        return ConstructorNode(
                convertParameters(parameters),
                convertTypeParameters(typeParameters)
        )
    }

    private fun EnumDeclaration.convert(): EnumNode {
        return EnumNode(
                name = IdentifierEntity(name),
                values = values.map { value -> EnumTokenNode(value.value, value.meta) },
                uid = uid,
                external = false
        )
    }

    private fun TypeAliasDeclaration.convert(): TypeAliasNode {
        return TypeAliasNode(
                name = aliasName,
                typeReference = typeReference.convertToNode(),
                typeParameters = typeParameters.map { typeParameter ->
                    TypeValueNode(typeParameter.name, typeParameter.constraints.map { it.convertToNode() })
                },
                uid = uid,
                external = false
        )
    }

    private fun FunctionDeclaration.convert(inDeclaredModule: Boolean): FunctionNode {
        val hasExportModifier = hasExportModifier()
        val exportQualifier = if (hasDefaultModifier() && hasExportModifier) {
            JsDefault()
        } else null

        return FunctionNode(
                IdentifierEntity(name),
                convertParameters(parameters),
                type.convertToNode(),
                convertTypeParameters(typeParameters),
                exportQualifier,
                hasExportModifier,
                false,
                false,
                null,
                FunctionNodeContextIrrelevant(),
                uid,
                null,
                body,
                inDeclaredModule || hasDeclareModifier()
        )
    }

    fun lowerMethodSignatureDeclaration(declaration: MethodSignatureDeclaration): MemberNode? {
        val memberDeclaration = convertMethodSignatureDeclaration(declaration)
        return when (memberDeclaration) {
            is PropertyNode -> memberDeclaration
            is MethodNode -> memberDeclaration.copy(
                    parameters = memberDeclaration.parameters,
                    type = memberDeclaration.type.convertToNode()
            )
            else -> raiseConcern("unkown method signature") { null }
        }
    }

    private fun mergeTypeParameters(interfaceTypeParams: List<TypeParameterDeclaration>, ownTypeParams: List<TypeParameterDeclaration>): List<TypeParameterDeclaration> {
        val ownNames = ownTypeParams.map { typeParam -> typeParam.name }.toSet()
        return interfaceTypeParams.map { typeParam ->
            val name = typeParam.name
            //TODO: it's quite easy to break this, so consider to introduce "breaking" test and fix for it
            typeParam.copy(name = if (ownNames.contains(name)) {
                when (name) {
                    is IdentifierEntity -> {
                        name.copy(value = name.value + "0")
                    }
                    is QualifierEntity -> {
                        name.copy(right = name.right.copy(value = name.right.value + "0"))
                    }
                }
            } else {
                name
            })
        }
    }

    private fun unrollOptionalParams(parameters: List<ParameterDeclaration>): List<List<ParameterNode>> {
        var (head, optionalTail) = convertParameters(parameters).partition { !it.optional }

        val generatedParams = mutableListOf(head)
        val tail = mutableListOf<ParameterNode>()
        while (optionalTail.isNotEmpty()) {
            tail.add(optionalTail[0].copy(optional = false, initializer = null))
            optionalTail = optionalTail.drop(1)
            generatedParams.add(head + tail)
        }

        return generatedParams
    }

    fun lowerMemberDeclaration(declaration: MemberEntity): List<MemberNode> {
        return when (declaration) {
            is FunctionDeclaration -> listOf(MethodNode(
                    declaration.name,
                    convertParameters(declaration.parameters),
                    declaration.type.convertToNode(),
                    convertTypeParameters(declaration.typeParameters),
                    declaration.isStatic(),
                    false,
                    true,
                    null,
                    declaration.body
            ))
            is MethodSignatureDeclaration -> listOf(lowerMethodSignatureDeclaration(declaration)).mapNotNull { it }
            is CallSignatureDeclaration -> listOf(declaration.convert())
            is PropertyDeclaration -> listOf(convertPropertyDeclaration(declaration))
            is IndexSignatureDeclaration -> convertIndexSignatureDeclaration(declaration)
            is ConstructorDeclaration -> listOf(declaration.convert())
            else -> raiseConcern("unkown member declaration ${this}") { emptyList<MemberNode>() }
        }
    }

    fun lowerVariableDeclaration(declaration: VariableDeclaration, inDeclaredModule: Boolean): TopLevelNode {
        val type = declaration.type
        return if (type is ObjectLiteralDeclaration) {

            if (type.canBeJson()) {
                VariableNode(
                        IdentifierEntity(declaration.name),
                        TypeValueNode(IdentifierEntity("Json"), emptyList()),
                        null,
                        false,
                        false,
                        emptyList(),
                        null,
                        declaration.uid,
                        null,
                        declaration.hasDeclareModifier()
                )
            } else {
                //TODO: don't forget to create owner
                val objectNode = ObjectNode(
                        IdentifierEntity(declaration.name),
                        type.members.flatMap { member -> lowerMemberDeclaration(member) },
                        emptyList(),
                        declaration.uid,
                        declaration.hasDeclareModifier()
                )

                objectNode.copy(members = objectNode.members.map {
                    when (it) {
                        is PropertyNode -> it.copy(open = false)
                        is MethodNode -> it.copy(open = false)
                        else -> it
                    }
                })
            }
        } else {
            VariableNode(
                    IdentifierEntity(declaration.name),
                    type.convertToNode(),
                    null,
                    false,
                    false,
                    emptyList(),
                    null,
                    declaration.uid,
                    null,
                    inDeclaredModule || declaration.hasDeclareModifier()
            )
        }
    }

    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity, ownerPackageName: NameEntity?, inDeclaredModule: Boolean): List<TopLevelNode> {
        return when (declaration) {
            is VariableDeclaration -> listOf(lowerVariableDeclaration(declaration, inDeclaredModule))
            is FunctionDeclaration -> listOf(declaration.convert(inDeclaredModule))
            is ClassDeclaration -> listOf(declaration.convert(inDeclaredModule))
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> listOf(declaration.convert())
            is ModuleDeclaration -> listOf(lowerPackageDeclaration(declaration, ownerPackageName, inDeclaredModule))
            is EnumDeclaration -> listOf(declaration.convert())
            is TypeAliasDeclaration -> listOf(declaration.convert())
            else -> listOf()
        }
    }


    private fun IdentifierEntity.convert(): IdentifierEntity {
        return IdentifierEntity(value)
    }

    private fun NameEntity.convert(): NameEntity {
        return when (this) {
            is IdentifierEntity -> convert()
            is QualifierEntity -> QualifierEntity(
                    left = left.convert(),
                    right = right.convert()
            )
        }
    }

    private fun NameEntity.isStringLiteral(): Boolean {
        return when (this) {
            is IdentifierEntity -> value.matches("^[\"\'].*[\"\']$".toRegex())
            else -> false
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun lowerPackageDeclaration(documentRoot: ModuleDeclaration, ownerPackageName: NameEntity?, isDeclaration: Boolean): ModuleNode {

        val name = documentRoot.name ?: if (documentRoot.isLib) {
            TSLIBROOT
        } else {
            IdentifierEntity("<ROOT>")
        }

        val shortName = name.unquote()
        val fullPackageName = ownerPackageName?.appendLeft(shortName) ?: shortName

        val imports = mutableMapOf<String, ImportNode>()
        val nonImports = mutableListOf<TopLevelNode>()

        val defaultEntityUid = documentRoot.export?.let {
            if (it.isExportEquals) {
                null
            } else {
                it.name
            }
        }

        documentRoot.declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports[declaration.name] = ImportNode(
                        declaration.moduleReference.convert(),
                        declaration.uid
                )
            } else {
                val topLevelNodes = lowerTopLevelDeclaration(declaration, fullPackageName, isDeclaration).map { topLevelNode ->
                    if ((topLevelNode is ExportableNode) && (topLevelNode.uid == defaultEntityUid)) {
                        topLevelNode.exportQualifier = JsDefault()
                    }
                    topLevelNode
                }

                nonImports.addAll(topLevelNodes)
            }
        }

        val moduleNameIsStringLiteral = name.isStringLiteral()

        val moduleName = if (moduleNameIsStringLiteral) {
            name.process { unquote(it) }
        } else {
            moduleNameResolver.resolveName(fileName)?.let { IdentifierEntity(it) }
        }

        return ModuleNode(
                moduleName = moduleName,
                export =  documentRoot.export?.let { ExportAssignmentNode(it.name, it.isExportEquals) },
                packageName = name,
                qualifiedPackageName = fullPackageName,
                declarations = nonImports,
                imports = imports,
                moduleNameIsStringLiteral = moduleNameIsStringLiteral,
                jsModule = null,
                jsQualifier = null,
                uid = documentRoot.uid,
                external = isDeclaration
        )
    }
}



class IntroduceNodes(private val moduleNameResolver: ModuleNameResolver) : Lowering<SourceSetDeclaration, SourceSetNode> {

    private fun ModuleDeclaration.introduceNodes(fileName: String, isInExternalDeclaration: Boolean) = LowerDeclarationsToNodes(fileName, moduleNameResolver).lowerPackageDeclaration(this, null, isInExternalDeclaration)

    private fun SourceFileDeclaration.introduceNodes(): SourceFileNode {
        val isInExternalDeclaration = fileName.endsWith(".d.ts")
        val references = root.imports.map { it.referencedFile } + root.references.map { it.referencedFile }

        return SourceFileNode(
                fileName,
                root.introduceNodes(fileName, isInExternalDeclaration),
                references,
                null
        )
    }

    override fun lower(source: SourceSetDeclaration): SourceSetNode {
        return SourceSetNode(sourceName = source.sourceName, sources = source.sources.map { sourceFile ->
            sourceFile.introduceNodes()
        })
    }
}