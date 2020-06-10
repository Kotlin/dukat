package org.jetbrains.dukat.nodeIntroduction

import org.jetbrains.dukat.ast.model.makeNullable
import org.jetbrains.dukat.ast.model.nodes.ClassNode
import org.jetbrains.dukat.ast.model.nodes.ConstructorNode
import org.jetbrains.dukat.ast.model.nodes.EnumNode
import org.jetbrains.dukat.ast.model.nodes.EnumTokenNode
import org.jetbrains.dukat.ast.model.nodes.ExportAssignmentNode
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
import org.jetbrains.dukat.ast.model.nodes.LiteralUnionNode
import org.jetbrains.dukat.ast.model.nodes.TopLevelNode
import org.jetbrains.dukat.ast.model.nodes.TupleTypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeAliasNode
import org.jetbrains.dukat.ast.model.nodes.TypeNode
import org.jetbrains.dukat.ast.model.nodes.TypeParameterNode
import org.jetbrains.dukat.ast.model.nodes.TypeValueNode
import org.jetbrains.dukat.ast.model.nodes.UnionLiteralKind
import org.jetbrains.dukat.ast.model.nodes.UnionTypeNode
import org.jetbrains.dukat.ast.model.nodes.VariableNode
import org.jetbrains.dukat.ast.model.nodes.constants.SELF_REFERENCE_TYPE
import org.jetbrains.dukat.ast.model.nodes.export.ExportQualifier
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
import org.jetbrains.dukat.tsmodel.ModuleDeclarationKind
import org.jetbrains.dukat.tsmodel.ParameterDeclaration
import org.jetbrains.dukat.tsmodel.PropertyDeclaration
import org.jetbrains.dukat.tsmodel.ReferenceOriginDeclaration
import org.jetbrains.dukat.tsmodel.SourceFileDeclaration
import org.jetbrains.dukat.tsmodel.SourceSetDeclaration
import org.jetbrains.dukat.tsmodel.ThisTypeDeclaration
import org.jetbrains.dukat.tsmodel.TypeAliasDeclaration
import org.jetbrains.dukat.tsmodel.TypeParameterDeclaration
import org.jetbrains.dukat.tsmodel.VariableDeclaration
import org.jetbrains.dukat.tsmodel.WithModifiersDeclaration
import org.jetbrains.dukat.tsmodel.types.FunctionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.IndexSignatureDeclaration
import org.jetbrains.dukat.tsmodel.types.IntersectionTypeDeclaration
import org.jetbrains.dukat.tsmodel.types.NumericLiteralDeclaration
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

private fun ParameterDeclaration.convertToNode(context: PARAMETER_CONTEXT = PARAMETER_CONTEXT.IRRELEVANT): ParameterNode {
    val parameterValueDeclaration = if (vararg && context == PARAMETER_CONTEXT.IRRELEVANT) {
        type.extractVarargType()
    } else {
        type
    }
    return ParameterNode(
            name = name,
            type = parameterValueDeclaration.convertToNode(),
            initializer = if (initializer != null || optional) {
                TypeValueNode(IdentifierEntity("definedExternally"), emptyList())
            } else null,
            meta = null,
            vararg = vararg,
            optional = optional
    )
}

private fun UnionTypeDeclaration.canBeTranslatedAsStringLiteral(): Boolean {
    return params.all { it is StringLiteralDeclaration }
}

private fun UnionTypeDeclaration.canBeTranslatedAsNumericLiteral(): Boolean {
    return params.all { it is NumericLiteralDeclaration }
}

private fun ParameterValueDeclaration.convertToNodeNullable(meta: ParameterValueDeclaration? = null): TypeNode? {
    return when (this) {
        is TypeParamReferenceDeclaration -> TypeParameterNode(
                name = value,
                nullable = nullable,
                meta = meta ?: meta
        )
        is TypeDeclaration -> TypeValueNode(
                value = value,
                params = params.map { param -> param.convertToNode() },
                typeReference = reference?.let {
                    ReferenceNode(it.uid)
                },
                nullable = nullable,
                meta = meta ?: meta
        )
        //TODO: investigate where we still have FunctionTypeDeclarations up to this point
        is FunctionTypeDeclaration -> FunctionTypeNode(
                parameters = parameters.map { parameterDeclaration ->
                    parameterDeclaration.convertToNode(PARAMETER_CONTEXT.FUNCTION_TYPE)
                },
                type = type.convertToNode(),
                nullable = nullable,
                meta = meta ?: meta
        )
        is GeneratedInterfaceReferenceDeclaration -> GeneratedInterfaceReferenceNode(
                name,
                typeParameters,
                reference,
                nullable,
                meta ?: meta
        )
        is IntersectionTypeDeclaration -> {
            val firstParam = params[0].convertToNodeNullable(IntersectionMetadata(params.map { it.convertToNodeNullable() ?: it }))
            if (nullable) {
                firstParam?.makeNullable()
            } else {
                firstParam
            }
        }
        is StringLiteralDeclaration -> {
            LiteralUnionNode(
                    params = listOf(token),
                    kind = UnionLiteralKind.STRING,
                    nullable = nullable
            )
        }
        is NumericLiteralDeclaration -> {
            LiteralUnionNode(
                    params = listOf(token),
                    kind = UnionLiteralKind.NUMBER,
                    nullable = nullable
            )
        }
        is UnionTypeDeclaration ->
            when {
                canBeTranslatedAsStringLiteral() -> {
                    LiteralUnionNode(
                            params = params.mapNotNull { (it as? StringLiteralDeclaration)?.token },
                            kind = UnionLiteralKind.STRING,
                            nullable = nullable
                    )
                }
                canBeTranslatedAsNumericLiteral() -> {
                    LiteralUnionNode(
                            params = params.mapNotNull { (it as? NumericLiteralDeclaration)?.token },
                            kind = UnionLiteralKind.NUMBER,
                            nullable = nullable
                    )
                }
                else -> {
                    UnionTypeNode(
                            params = params.map { param -> param.convertToNode() },
                            nullable = nullable,
                            meta = meta ?: meta
                    ).lowerAsNullable()
                }
            }
        is TupleDeclaration -> TupleTypeNode(
                params = params.map { param -> param.convertToNode() },
                nullable = nullable,
                meta = meta ?: meta
        )
        is ThisTypeDeclaration -> SELF_REFERENCE_TYPE
        is TypeNode -> this
        else -> null
    }
}

private val TYPE_ANY = TypeValueNode(IdentifierEntity("Any"), emptyList(), null, false)

private fun ParameterValueDeclaration.convertToNode(meta: ParameterValueDeclaration? = null): TypeNode {
    return convertToNodeNullable(meta) ?: TYPE_ANY
}

private fun UnionTypeNode.resolveAsNullableType(): TypeNode? {
    val paramsFiltered = params.filter { param ->
        when (param) {
            is TypeValueNode -> {
                val value = param.value
                value != IdentifierEntity("undefined") && value != IdentifierEntity("null")
            }
            else -> true
        }
    }

    return if ((paramsFiltered.size < params.size)) {
        if (paramsFiltered.size == 1) {
            paramsFiltered[0]
        } else {
            copy(params = paramsFiltered, nullable = true)
        }
    } else {
        null
    }
}

private fun UnionTypeNode.lowerAsNullable(): TypeNode {
    return resolveAsNullableType()?.let { nullableType ->
        when (nullableType) {
            is TypeValueNode -> nullableType.copy(nullable = true, meta = null)
            is TypeParameterNode -> nullableType.copy(nullable = true)
            is FunctionTypeNode -> nullableType.copy(nullable = true, meta = null)
            is UnionTypeNode -> nullableType
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
        val parameterValueDeclaration = if (declaration.optional) declaration.type.makeNullable() else declaration.type
        return PropertyNode(
                name = declaration.name,
                type = parameterValueDeclaration.convertToNode(),
                typeParameters = convertTypeParameters(declaration.typeParameters),

                static = declaration.isStatic(),
                initializer = declaration.initializer,
                getter = declaration.optional,
                setter = declaration.optional,  // TODO: it's actually wrong

                open = true
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
                    null,
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
                    null,
                    false
            )
        }
    }


    private fun convertIndexSignatureDeclaration(declaration: IndexSignatureDeclaration): List<MethodNode> {
        val parameterValueDeclaration = declaration.returnType.makeNullable()
        return listOf(
                MethodNode(
                        "get",
                        convertParameters(declaration.indexTypes),
                        parameterValueDeclaration.convertToNode(),
                        emptyList(),
                        false,
                        true,
                        true,
                        null,
                        null,
                        false
                ),
                MethodNode(
                        "set",
                        convertParameters(declaration.indexTypes + listOf(ParameterDeclaration("value", declaration.returnType.convertToNodeNullable() ?: declaration.returnType, null, false, false))),
                        TypeValueNode(IdentifierEntity("Unit"), emptyList()),
                        emptyList(),
                        false,
                        true,
                        true,
                        null,
                        null,
                        false
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
                null,
                false
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
        val declaration = ClassNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                typeParameters.map { typeParameter ->
                    TypeValueNode(typeParameter.name, typeParameter.constraints.map { it.convertToNode() })
                },
                convertToHeritageNodes(parentEntities),
                null,

                uid,
                resolveAsExportQualifier(),
                inDeclaredModule || hasDeclareModifier()
        )

        return declaration
    }

    private fun InterfaceDeclaration.convert(): TopLevelNode {
        return InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
                false,
                uid,
                true
        )
    }


    private fun GeneratedInterfaceDeclaration.convert(): InterfaceNode {
        val declaration = InterfaceNode(
                name,
                members.flatMap { member -> lowerMemberDeclaration(member) },
                convertTypeParameters(typeParameters),
                convertToHeritageNodes(parentEntities),
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

    private fun WithModifiersDeclaration.resolveAsExportQualifier(): ExportQualifier? {
        return if (hasDefaultModifier() && hasExportModifier()) {
            JsDefault()
        } else null
    }

    private fun FunctionDeclaration.convert(inDeclaredModule: Boolean): FunctionNode {

        return FunctionNode(
                IdentifierEntity(name),
                convertParameters(parameters),
                type.convertToNode(),
                convertTypeParameters(typeParameters),
                resolveAsExportQualifier(),
                hasExportModifier(),
                false,
                false,
                null,
                FunctionNodeContextIrrelevant(),
                uid,
                null,
                body,
                inDeclaredModule || hasDeclareModifier(),
                isGenerator
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
                    declaration.body,
                    declaration.isGenerator
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
                        declaration.resolveAsExportQualifier(),
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
                    declaration.resolveAsExportQualifier(),
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

    private fun lowerTopLevelDeclaration(declaration: TopLevelEntity, ownerPackageName: NameEntity?, inDeclaredModule: Boolean): TopLevelNode? {
        return when (declaration) {
            is VariableDeclaration -> lowerVariableDeclaration(declaration, inDeclaredModule)
            is FunctionDeclaration -> declaration.convert(inDeclaredModule)
            is ClassDeclaration -> declaration.convert(inDeclaredModule)
            is InterfaceDeclaration -> declaration.convert()
            is GeneratedInterfaceDeclaration -> declaration.convert()
            is ModuleDeclaration -> lowerPackageDeclaration(declaration, ownerPackageName, inDeclaredModule)
            is EnumDeclaration -> declaration.convert()
            is TypeAliasDeclaration -> declaration.convert()
            else -> null
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



        documentRoot.declarations.forEach { declaration ->
            if (declaration is ImportEqualsDeclaration) {
                imports[declaration.name] = ImportNode(
                        declaration.moduleReference.convert(),
                        declaration.uid
                )
            } else {
                lowerTopLevelDeclaration(declaration, fullPackageName, isDeclaration)?.let { nonImports.add(it) }
            }
        }

        val moduleNameIsStringLiteral = name.isStringLiteral()

        val moduleName = if (moduleNameIsStringLiteral) {
            name.process { unquote(it) }
        } else {
            moduleNameResolver.resolveName(fileName)?.let { IdentifierEntity(it) }
        }

        val hasDefaultExport = documentRoot.declarations.any {
            (it is WithModifiersDeclaration) && (it !is InterfaceDeclaration) && (it.hasExportModifier()) && (it.hasDefaultModifier())
         }

        return ModuleNode(
                moduleName = moduleName,
                export = documentRoot.export?.let { ExportAssignmentNode(it.uids, it.isExportEquals) },
                packageName = name,
                qualifiedPackageName = fullPackageName,
                declarations = nonImports,
                imports = imports,
                moduleNameIsStringLiteral = moduleNameIsStringLiteral,
                jsModule = if (hasDefaultExport) {
                    moduleName
                } else {
                    null
                },
                jsQualifier = null,
                uid = documentRoot.uid,
                external = isDeclaration
        )
    }
}


class IntroduceNodes(private val moduleNameResolver: ModuleNameResolver) : Lowering<SourceSetDeclaration, SourceSetNode> {

    private fun ModuleDeclaration.introduceNodes(fileName: String, isInExternalDeclaration: Boolean) = LowerDeclarationsToNodes(fileName, moduleNameResolver).lowerPackageDeclaration(this, null, isInExternalDeclaration)

    private fun SourceFileDeclaration.introduceNodes(): SourceFileNode {
        val references = root.imports.map { it.referencedFile } + root.references.map { it.referencedFile }

        return SourceFileNode(
                fileName,
                root.introduceNodes(fileName, root.kind == ModuleDeclarationKind.DECLARATION_FILE),
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