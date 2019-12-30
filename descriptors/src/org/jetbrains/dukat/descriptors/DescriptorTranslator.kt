package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astCommon.NameEntity
import org.jetbrains.dukat.astCommon.appendLeft
import org.jetbrains.dukat.astCommon.leftMost
import org.jetbrains.dukat.astCommon.shiftLeft
import org.jetbrains.dukat.astCommon.shiftRight
import org.jetbrains.dukat.astModel.AnnotationModel
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassLikeReferenceModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeParameterReferenceModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translator.ROOT_PACKAGENAME
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.builtins.KotlinBuiltIns.FQ_NAMES
import org.jetbrains.kotlin.builtins.createFunctionType
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProviderImpl
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptorImpl
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassConstructorDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.EmptyPackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.impl.EnumEntrySyntheticClassDescriptor
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyGetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertySetterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.js.resolve.JsPlatformAnalyzerServices.builtIns
import org.jetbrains.kotlin.load.java.components.DescriptorResolverUtils
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.StringValue
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyTypeAliasDescriptor
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.StaticScopeForKotlinEnum
import org.jetbrains.kotlin.serialization.deserialization.ErrorReporter
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.storage.NotNullLazyValue
import org.jetbrains.kotlin.types.AbbreviatedType
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.LazyWrappedType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.StarProjectionImpl
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeProjectionImpl
import org.jetbrains.kotlin.types.TypeSubstitution
import org.jetbrains.kotlin.types.TypeSubstitutor
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.createDynamicType
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.replaceAnnotations

internal fun translateName(name: NameEntity): String {
    return name.translate().replace("`", "")
}

fun translatePackageName(name: NameEntity): FqName {
    return FqName(
        when (name) {
            ROOT_PACKAGENAME -> ""
            else -> translateName(name)
        }
    )
}

private class DescriptorTranslator(val context: DescriptorContext) {

    private fun findClassInStdlib(typeModel: TypeValueModel): ClassDescriptor? {
        val packageNames = context.registeredImports.map { FqName(it) }
        val stdlibModule = context.config.moduleDescriptors.first { it.name == Name.special("<kotlin>") }
        return (packageNames + FqName("kotlin") + FqName("kotlin.collections")).map { packageName ->
            val packageDescriptor = stdlibModule.getPackage(packageName)
            packageDescriptor.fragments.mapNotNull { fragment ->
                fragment.getMemberScope().getContributedClassifier(
                    Name.identifier(translateName(typeModel.value)),
                    NoLookupLocation.FROM_TEST
                )
            }
        }.flatten().filterIsInstance<ClassDescriptor>().firstOrNull()
    }

    private fun findClass(typeModel: TypeValueModel): ClassDescriptor? {
        return context.getDescriptor(typeModel.fqName ?: IdentifierEntity("<ROOT>").appendLeft(typeModel.value))
            ?: when (translateName(typeModel.value)) {
                "@@None" -> builtIns.getBuiltInClassByFqName(FQ_NAMES.unit.toSafe())
                else -> findClassInStdlib(typeModel)
            }
    }

    private fun translateVariance(variance: org.jetbrains.dukat.astModel.Variance): Variance {
        return when (variance) {
            org.jetbrains.dukat.astModel.Variance.INVARIANT -> Variance.INVARIANT
            org.jetbrains.dukat.astModel.Variance.COVARIANT -> Variance.OUT_VARIANCE
            org.jetbrains.dukat.astModel.Variance.CONTRAVARIANT -> Variance.IN_VARIANCE
        }
    }

    private fun generateReplacementTypeArguments(
        typeModel: TypeValueModel,
        typeProjectionTypes: List<KotlinType?>,
        oldTypeArguments: List<TypeParameterDescriptor>
    ): List<TypeProjection> {
        return typeModel.params.zip(typeProjectionTypes).mapIndexed { index, (model, projectionType) ->
            if (model.type is TypeValueModel && (model.type as TypeValueModel).value == IdentifierEntity(
                    "*"
                )
            ) {
                StarProjectionImpl(oldTypeArguments[index])
            } else {
                TypeProjectionImpl(
                    translateVariance(model.variance),
                    projectionType!!
                )
            }
        }
    }

    private fun translateType(typeModel: TypeModel, shouldExpand: Boolean = true): KotlinType {
        if (typeModel is TypeParameterReferenceModel) {
            return context.getTypeParameter(typeModel.name)?.defaultType?.makeNullableAsSpecified(typeModel.nullable)!!
        }
        if (typeModel is TypeValueModel) {
            val typeProjectionTypes = typeModel.params.map {
                if (it.type is TypeValueModel && (it.type as TypeValueModel).value == IdentifierEntity("*")) {
                    null
                } else {
                    translateType(it.type, shouldExpand)
                }
            }
            return context.getTypeParameter(typeModel.value)?.defaultType?.makeNullableAsSpecified(typeModel.nullable)
                ?: LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                    val typeAlias = context.getTypeAlias(typeModel.value)
                    if (typeAlias != null) {
                        val typeParameters = typeAlias.defaultType.constructor.parameters
                        val newTypeArguments = generateReplacementTypeArguments(
                            typeModel,
                            typeProjectionTypes,
                            typeParameters
                        )
                        if (shouldExpand) {
                            (expandTypeAlias(
                                context,
                                typeAlias.defaultType,
                                typeParameters,
                                newTypeArguments
                            ).type as SimpleType).makeNullableAsSpecified(typeModel.nullable)
                        } else {
                            typeAlias.defaultType.replace(newTypeArguments).makeNullableAsSpecified(typeModel.nullable)
                        }
                    } else {
                        val classDescriptor = findClass(typeModel)
                        if (classDescriptor == null) {
                            if (typeModel.value == IdentifierEntity("dynamic")) {
                                createDynamicType(builtIns)
                            } else {
                                ErrorUtils.createErrorType(translateName(typeModel.value))
                                    .makeNullableAsSpecified(typeModel.nullable)
                            }
                        } else {
                            KotlinTypeFactory.simpleType(
                                annotations = Annotations.EMPTY,
                                constructor = classDescriptor.defaultType.constructor,
                                arguments = generateReplacementTypeArguments(
                                    typeModel,
                                    typeProjectionTypes,
                                    classDescriptor.declaredTypeParameters
                                ),
                                nullable = typeModel.nullable
                            )
                        }
                    }
                }
        }
        if (typeModel is FunctionTypeModel) {
            val parameterTypes = typeModel.parameters.map { translateType(it.type, shouldExpand) }
            val returnType = translateType(typeModel.type, shouldExpand)
            return LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                createFunctionType(
                    builtIns = builtIns,
                    annotations = Annotations.EMPTY,
                    receiverType = null,
                    parameterTypes = parameterTypes,
                    parameterNames = typeModel.parameters.map {
                        Name.identifier(translateName(IdentifierEntity(it.name)))
                    },
                    returnType = returnType
                ).makeNullableAsSpecified(typeModel.nullable)
            }
        }
        return ErrorUtils.createErrorType("NOT_IMPLEMENTED")
    }

    private fun translateParameters(
        parameterModels: List<ParameterModel>,
        parent: FunctionDescriptor
    ): List<ValueParameterDescriptorImpl> {
        return parameterModels.mapIndexed { index, parameter ->
            val type = translateType(parameter.type)
            val outType = LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                if (parameter.vararg) {
                    builtIns.getPrimitiveArrayKotlinTypeByPrimitiveKotlinType(
                        type
                    ) ?: builtIns.getArrayType(Variance.OUT_VARIANCE, type)
                } else {
                    type
                }
            }
            val varargElementType = if (parameter.vararg) {
                type
            } else {
                null
            }
            ValueParameterDescriptorImpl(
                containingDeclaration = parent,
                original = null,
                index = index,
                annotations = Annotations.EMPTY,
                name = Name.identifier(translateName(IdentifierEntity(parameter.name))),
                outType = outType,
                declaresDefaultValue = parameter.initializer != null,
                isCrossinline = false,
                isNoinline = false,
                varargElementType = varargElementType,
                source = parent.source
            )
        }
    }


    private fun translateTypeParameters(
        parameters: List<TypeParameterModel>,
        parent: DeclarationDescriptor
    ): List<TypeParameterDescriptor> {
        val parametersDescriptors = parameters.mapIndexed { index, parameter ->
            val parameterDescriptor = TypeParameterDescriptorImpl.createForFurtherModification(
                parent,
                Annotations.EMPTY,
                false,
                translateVariance(parameter.variance),
                Name.identifier(translateName((parameter.type as TypeValueModel).value)),
                index,
                parent.toSourceElement
            )
            context.registerTypeParameter(
                IdentifierEntity(parameterDescriptor.name.identifier),
                parameterDescriptor
            )
            parameterDescriptor
        }
        parametersDescriptors.zip(parameters).forEach { (descriptor, model) ->
            model.constraints.forEach {
                val type = translateType(it)
                if (type is LazyWrappedType) {
                    context.addConstraintInitialization(descriptor, type)
                } else {
                    descriptor.addUpperBound(type)
                }
            }
            if (context.canBeInitialized(descriptor)) {
                descriptor.addDefaultUpperBound()
                descriptor.setInitialized()
            }
        }
        return parametersDescriptors
    }

    private fun translateTypeAlias(
        typeAliasModel: TypeAliasModel,
        parent: PackageFragmentDescriptor
    ): TypeAliasDescriptor {
        val typeAliasDescriptor = LazyTypeAliasDescriptor.create(
            LockBasedStorageManager.NO_LOCKS,
            NoScopeRecordCliBindingTrace(),
            parent,
            Annotations.EMPTY,
            Name.identifier(translateName(typeAliasModel.name)),
            SourceElement.NO_SOURCE,
            Visibilities.PUBLIC
        )
        val typeParameters = translateTypeParameters(typeAliasModel.typeParameters, typeAliasDescriptor)
        fun computeUnderlyingType(shouldExpand: Boolean): NotNullLazyValue<SimpleType> {
            return LockBasedStorageManager.NO_LOCKS.createLazyValue {
                typeParameters.forEach {
                    context.registerTypeParameter(IdentifierEntity(it.name.identifier), it)

                }
                val result = translateType(typeAliasModel.typeReference, shouldExpand).unwrap() as SimpleType
                typeParameters.forEach {
                    context.removeTypeParameter(IdentifierEntity(it.name.identifier))
                }
                result
            }
        }
        typeAliasDescriptor.initialize(
            typeParameters,
            computeUnderlyingType(shouldExpand = false),
            computeUnderlyingType(shouldExpand = true)
        )
        context.registerTypeAlias(typeAliasModel.name, typeAliasDescriptor)
        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }
        return typeAliasDescriptor
    }

    private fun translateAnnotations(annotationModels: List<AnnotationModel>): Annotations {
        return Annotations.create(annotationModels.map { annotationModel ->
            val typeModel = TypeValueModel(
                value = IdentifierEntity(annotationModel.name),
                params = listOf(),
                metaDescription = null,
                fqName = null
            )
            val annotationClassDescriptor = findClass(typeModel)
            val primaryConstructor = annotationClassDescriptor?.constructors?.first()
            // for now we will assume that all parameters are String
            val parameterValues = annotationModel.params.map { StringValue(it.translate()) }
            AnnotationDescriptorImpl(
                translateType(typeModel),
                primaryConstructor?.valueParameters?.map { it.name }?.zip(
                    if (parameterValues.isNotEmpty() && primaryConstructor.valueParameters[0].isVararg) {
                        listOf(ArrayValue(parameterValues) { module ->
                            module.builtIns.getPrimitiveArrayKotlinTypeByPrimitiveKotlinType(
                                module.builtIns.stringType
                            )!!
                        })
                    } else {
                        parameterValues
                    }
                )?.toMap().orEmpty(),
                SourceElement.NO_SOURCE
            )
        })
    }

    private fun translateHeritage(heritageModel: HeritageModel): KotlinType? {
        return translateType(
            TypeValueModel(
                value = heritageModel.value.value,
                params = heritageModel.typeParams.map {
                    TypeParameterModel(
                        it,
                        listOf()
                    )
                },
                metaDescription = null,
                fqName = heritageModel.value.fqName
            )
        )
    }

    private fun translateExtensionReceiver(
        extensionReceiver: ClassLikeReferenceModel,
        callableDescriptor: CallableDescriptor
    ): ReceiverParameterDescriptor? {
        return DescriptorFactory.createExtensionReceiverParameterForCallable(
            callableDescriptor,
            translateType(
                TypeValueModel(
                    value = extensionReceiver.name,
                    params = extensionReceiver.typeParameters.map {
                        TypeParameterModel(
                            type = TypeValueModel(
                                value = it,
                                params = listOf(),
                                metaDescription = null,
                                fqName = null
                            ), constraints = listOf()
                        )
                    },
                    metaDescription = null,
                    fqName = null
                )
            ),
            Annotations.EMPTY
        )

    }

    private fun translateMethod(methodModel: MethodModel, parent: ClassDescriptor): FunctionDescriptor {
        val functionDescriptor = object : SimpleFunctionDescriptorImpl(
            parent,
            null,
            translateAnnotations(methodModel.annotations),
            Name.identifier(translateName(methodModel.name)),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE
        ) {
            override fun getOverriddenDescriptors(): MutableCollection<out FunctionDescriptor> {
                if (methodModel.override != null) {
                    var overriddenMethod = context.resolveMethod(
                        methodModel.override as NameEntity,
                        methodModel.name
                    )
                    if (overriddenMethod == null) {
                        var override = methodModel.override as NameEntity
                        if (override.leftMost() == IdentifierEntity("<LIBROOT>")) {
                            override = override.shiftLeft()!!
                        }
                        val classInStdLib = findClassInStdlib(
                            TypeValueModel(
                                value = override,
                                params = listOf(),
                                metaDescription = null,
                                fqName = null
                            )
                        )
                        overriddenMethod = classInStdLib?.unsubstitutedMemberScope?.getContributedFunctions(
                            Name.identifier(translateName(methodModel.name)), NoLookupLocation.FROM_BUILTINS
                        )?.single()
                    }
                    return listOfNotNull(overriddenMethod).toMutableList()
                }
                return super.getOverriddenDescriptors()
            }
        }
        context.registerMethod(context.currentPackageName.appendLeft(methodModel.name), functionDescriptor)
        val typeParameters = translateTypeParameters(methodModel.typeParameters, functionDescriptor)
        functionDescriptor.initialize(
            null,
            null,
            typeParameters,
            translateParameters(methodModel.parameters, functionDescriptor),
            translateType(methodModel.type),
            when (parent.kind) {
                ClassKind.INTERFACE -> Modality.ABSTRACT
                ClassKind.OBJECT -> Modality.FINAL
                else -> if (methodModel.open) Modality.OPEN else Modality.FINAL
            },
            Visibilities.PUBLIC
        )
        functionDescriptor.isOperator = methodModel.operator

        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }

        return functionDescriptor
    }

    private fun translateFunction(
        functionModel: FunctionModel,
        parent: PackageFragmentDescriptor
    ): FunctionDescriptor {
        val functionDescriptor = SimpleFunctionDescriptorImpl.create(
            parent,
            translateAnnotations(functionModel.annotations),
            Name.identifier(translateName(functionModel.name)),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE
        )
        val typeParameters = translateTypeParameters(functionModel.typeParameters, functionDescriptor)
        functionDescriptor.initialize(
            functionModel.extend?.let {
                translateExtensionReceiver(it, functionDescriptor)
            },
            null,
            typeParameters,
            translateParameters(functionModel.parameters, functionDescriptor),
            translateType(functionModel.type),
            Modality.FINAL,
            Visibilities.PUBLIC
        )
        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }
        functionDescriptor.isInline = functionModel.inline
        functionDescriptor.isExternal = !functionModel.inline
        return functionDescriptor
    }

    private fun translateConstructor(
        constructorModel: ConstructorModel,
        parent: ClassDescriptor,
        isPrimary: Boolean,
        visibility: Visibility
    ): ClassConstructorDescriptor {
        val constructorDescriptor = ClassConstructorDescriptorImpl.create(
            parent,
            Annotations.EMPTY,
            isPrimary,
            SourceElement.NO_SOURCE
        )
        constructorDescriptor.initialize(
            translateParameters(constructorModel.parameters, constructorDescriptor),
            visibility
        )
        constructorDescriptor.returnType = LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
            parent.defaultType
        }
        return constructorDescriptor
    }

    private fun translateProperty(propertyModel: PropertyModel, parent: ClassDescriptor): PropertyDescriptor {
        val propertyDescriptor = object : PropertyDescriptorImpl(
            parent,
            null,
            Annotations.EMPTY,
            when {
                propertyModel.getter || propertyModel.setter -> Modality.OPEN
                parent.kind == ClassKind.INTERFACE -> Modality.ABSTRACT
                parent.kind == ClassKind.OBJECT -> Modality.FINAL
                propertyModel.open -> Modality.OPEN
                else -> Modality.FINAL
            },
            Visibilities.PUBLIC,
            !propertyModel.immutable,
            Name.identifier(translateName(propertyModel.name)),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE,
            false,
            false,
            false,
            false,
            false,
            false
        ) {
            override fun getOverriddenDescriptors(): MutableCollection<out PropertyDescriptor> {
                if (propertyModel.override != null) {
                    var overriddenProperty = context.resolveProperty(
                        propertyModel.override as NameEntity,
                        propertyModel.name
                    ) as PropertyDescriptor?
                    if (overriddenProperty == null) {
                        var override = propertyModel.override as NameEntity
                        if (override.leftMost() == IdentifierEntity("<LIBROOT>")) {
                            override = override.shiftLeft()!!
                        }
                        val classInStdLib = findClassInStdlib(
                            TypeValueModel(
                                value = override,
                                params = listOf(),
                                metaDescription = null,
                                fqName = null
                            )
                        )
                        overriddenProperty = classInStdLib?.unsubstitutedMemberScope?.getContributedVariables(
                            Name.identifier(translateName(propertyModel.name)), NoLookupLocation.FROM_BUILTINS
                        )?.single()
                    }
                    return listOfNotNull(overriddenProperty).toMutableList()
                }
                return super.getOverriddenDescriptors()
            }
        }
        context.registerProperty(context.currentPackageName.appendLeft(propertyModel.name), propertyDescriptor)
        val typeParameters = translateTypeParameters(propertyModel.typeParameters, propertyDescriptor)
        propertyDescriptor.setType(
            translateType(propertyModel.type),
            typeParameters,
            null,
            null
        )
        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }
        val getter = if (parent.kind == ClassKind.INTERFACE && propertyModel.getter) {
            PropertyGetterDescriptorImpl(
                propertyDescriptor,
                Annotations.EMPTY,
                propertyDescriptor.modality,
                propertyDescriptor.visibility,
                false,
                false,
                false,
                CallableMemberDescriptor.Kind.DECLARATION,
                null,
                propertyDescriptor.source
            )
        } else {
            DescriptorFactory.createDefaultGetter(
                propertyDescriptor,
                Annotations.EMPTY
            )
        }
        getter.initialize(propertyDescriptor.type)
        val setter = if (parent.kind == ClassKind.INTERFACE && propertyModel.setter) {
            PropertySetterDescriptorImpl(
                propertyDescriptor,
                Annotations.EMPTY,
                propertyDescriptor.modality,
                propertyDescriptor.visibility,
                false,
                false,
                false,
                CallableMemberDescriptor.Kind.DECLARATION,
                null,
                propertyDescriptor.source
            ).also {
                it.initialize(
                    translateParameters(
                        listOf(
                            ParameterModel(
                                name = "value",
                                type = propertyModel.type,
                                initializer = null,
                                vararg = false
                            )
                        ), parent = it
                    ).first()
                )
            }

        } else if (!propertyModel.immutable) {
            DescriptorFactory.createDefaultSetter(
                propertyDescriptor,
                Annotations.EMPTY,
                Annotations.EMPTY
            )
        } else {
            null
        }
        propertyDescriptor.initialize(
            getter,
            setter
        )
        return propertyDescriptor
    }

    private fun translateVariable(
        variableModel: VariableModel,
        parent: PackageFragmentDescriptor
    ): PropertyDescriptor {
        val variableDescriptor = PropertyDescriptorImpl.create(
            parent,
            translateAnnotations(variableModel.annotations),
            Modality.FINAL,
            Visibilities.PUBLIC,
            !variableModel.immutable,
            Name.identifier(translateName(variableModel.name)),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE,
            false,
            false,
            false,
            false,
            !variableModel.inline,
            false
        )
        val typeParameters = translateTypeParameters(variableModel.typeParameters, variableDescriptor)
        variableDescriptor.setType(
            translateType(variableModel.type),
            typeParameters,
            null,
            variableModel.extend?.let {
                translateExtensionReceiver(it, variableDescriptor)
            }
        )
        val getter = DescriptorFactory.createGetter(
            variableDescriptor,
            Annotations.EMPTY,
            false,
            false,
            variableModel.inline
        )
        getter.initialize(variableDescriptor.type)
        val setter = if (!variableModel.immutable) {
            DescriptorFactory.createSetter(
                variableDescriptor,
                Annotations.EMPTY,
                Annotations.EMPTY,
                false,
                false,
                variableModel.inline,
                variableDescriptor.source
            )
        } else {
            null
        }
        variableDescriptor.initialize(
            getter,
            setter
        )
        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }
        return variableDescriptor
    }

    private fun translateMember(memberModel: MemberModel, parent: ClassDescriptor): DeclarationDescriptor? {
        return when (memberModel) {
            is MethodModel -> translateMethod(
                memberModel,
                parent
            )
            is PropertyModel -> translateProperty(
                memberModel,
                parent
            )
            is ClassLikeModel -> translateClassLike(
                memberModel,
                parent,
                isTopLevel = false
            )
            else -> null
        }
    }

    private fun translateClassLike(
        classLikeModel: ClassLikeModel,
        parent: DeclarationDescriptor,
        isTopLevel: Boolean
    ): ClassDescriptor {
        val companionDescriptor = classLikeModel.companionObject?.let {
            translateObject(it, parent, isCompanion = true, isTopLevel = false)
        }

        var typeParameters: List<TypeParameterDescriptor> = listOf()
        if (classLikeModel is ClassModel) {
            typeParameters = translateTypeParameters(classLikeModel.typeParameters, parent)
        }
        if (classLikeModel is InterfaceModel) {
            typeParameters = translateTypeParameters(classLikeModel.typeParameters, parent)
        }

        val parentTypes = classLikeModel.parentEntities.mapNotNull {
            translateHeritage(it)
        }

        val classDescriptor = CustomClassDescriptor(
            parent = parent,
            name = classLikeModel.name,
            modality = when (classLikeModel) {
                is ClassModel -> if (classLikeModel.abstract) Modality.ABSTRACT else Modality.OPEN
                is InterfaceModel -> Modality.ABSTRACT
                else -> Modality.FINAL
            },
            classKind = when (classLikeModel) {
                is ClassModel -> ClassKind.CLASS
                is InterfaceModel -> ClassKind.INTERFACE
                else -> ClassKind.CLASS
            },
            parentTypes = parentTypes,
            isCompanion = false,
            isTopLevel = isTopLevel,
            companionObject = companionDescriptor,
            typeParameters = typeParameters,
            annotations = translateAnnotations(classLikeModel.annotations)
        )
        context.registerDescriptor(classLikeModel.name, classDescriptor)
        context.currentPackageName = context.currentPackageName.appendLeft(classLikeModel.name)
        context.registerDelegations(classDescriptor, parentTypes, classLikeModel.parentEntities)


        var primaryConstructorDescriptor: ClassConstructorDescriptor? = null
        var constructorDescriptors: Set<ClassConstructorDescriptor> = setOf()
        if (classLikeModel is ClassModel) {
            val constructorModels = classLikeModel.members.filterIsInstance<ConstructorModel>()
            primaryConstructorDescriptor =
                if (classLikeModel.primaryConstructor == null && constructorModels.isEmpty()) {
                    translateConstructor(
                        ConstructorModel(listOf(), listOf()),
                        classDescriptor,
                        true,
                        Visibilities.PUBLIC
                    )
                } else {
                    classLikeModel.primaryConstructor?.let {
                        translateConstructor(
                            it,
                            classDescriptor,
                            true,
                            Visibilities.PUBLIC
                        )
                    }
                }
            constructorDescriptors = (constructorModels.map {
                translateConstructor(
                    it,
                    classDescriptor,
                    false,
                    Visibilities.PUBLIC
                )
            } + primaryConstructorDescriptor).filterNotNull().toSet()
        }

        classDescriptor.initialize(
            MutableMemberScope((classLikeModel.members.mapNotNull
            {
                translateMember(it, classDescriptor)
            } + companionDescriptor as DeclarationDescriptor?).filterNotNull().toMutableList()),
            constructorDescriptors,
            primaryConstructorDescriptor
        )

        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }

        context.currentPackageName = context.currentPackageName.shiftRight()!!

        return classDescriptor
    }

    private fun translateObject(
        objectModel: ObjectModel,
        parent: DeclarationDescriptor,
        isCompanion: Boolean,
        isTopLevel: Boolean
    ): ClassDescriptor {
        val parentTypes = objectModel.parentEntities.mapNotNull {
            translateHeritage(it)
        }
        val objectDescriptor = CustomClassDescriptor(
            parent = parent,
            name = objectModel.name,
            modality = Modality.FINAL,
            classKind = ClassKind.OBJECT,
            parentTypes = parentTypes,
            isCompanion = isCompanion,
            isTopLevel = isTopLevel,
            companionObject = null,
            typeParameters = listOf(),
            annotations = Annotations.EMPTY
        )
        context.registerDescriptor(objectModel.name, objectDescriptor)
        context.registerDelegations(objectDescriptor, parentTypes, objectModel.parentEntities)
        val privatePrimaryConstructor = translateConstructor(
            ConstructorModel(listOf(), listOf()),
            objectDescriptor,
            true,
            Visibilities.PRIVATE
        )

        objectDescriptor.initialize(
            MutableMemberScope(objectModel.members.mapNotNull {
                translateMember(it, objectDescriptor)
            }.toMutableList()),
            setOf(privatePrimaryConstructor),
            privatePrimaryConstructor
        )
        (privatePrimaryConstructor as ClassConstructorDescriptorImpl).returnType = objectDescriptor.defaultType
        return objectDescriptor
    }

    private fun translateEnum(enumModel: EnumModel, parent: PackageFragmentDescriptor): ClassDescriptor {
        val enumDescriptor = CustomClassDescriptor(
            parent = parent,
            name = enumModel.name,
            modality = Modality.FINAL,
            classKind = ClassKind.ENUM_CLASS,
            parentTypes = listOf(
                translateType(
                    TypeValueModel(
                        value = IdentifierEntity("Enum"),
                        params = listOf(
                            TypeParameterModel(
                                TypeValueModel(
                                    value = enumModel.name,
                                    params = listOf(),
                                    metaDescription = null,
                                    fqName = context.currentPackageName.appendLeft(enumModel.name)
                                ), constraints = listOf(), variance = org.jetbrains.dukat.astModel.Variance.INVARIANT
                            )
                        ),
                        metaDescription = null,
                        fqName = null
                    )
                )
            ),
            isCompanion = false,
            isTopLevel = true,
            companionObject = null,
            typeParameters = listOf(),
            annotations = Annotations.EMPTY
        )
        enumDescriptor.staticEnumScope = StaticScopeForKotlinEnum(
            LockBasedStorageManager.NO_LOCKS,
            enumDescriptor
        )
        context.registerDescriptor(
            enumModel.name, enumDescriptor
        )
        val privatePrimaryConstructor = translateConstructor(
            ConstructorModel(listOf(), listOf()),
            enumDescriptor,
            true,
            Visibilities.PRIVATE
        )
        val enumMemberNames = enumModel.values.map { Name.identifier(translateName(IdentifierEntity(it.value))) }
        enumDescriptor.initialize(
            MutableMemberScope(enumMemberNames.map {
                val constructor = EnumEntrySyntheticClassDescriptor::class.java.declaredConstructors[0]
                constructor.isAccessible = true
                constructor.newInstance(
                    LockBasedStorageManager.NO_LOCKS,
                    enumDescriptor,
                    LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                        enumDescriptor.defaultType
                    },
                    it,
                    LockBasedStorageManager.NO_LOCKS.createLazyValue {
                        enumMemberNames.toSet()
                    },
                    Annotations.EMPTY,
                    SourceElement.NO_SOURCE
                ) as EnumEntrySyntheticClassDescriptor
            }.toMutableList()),
            setOf(privatePrimaryConstructor),
            privatePrimaryConstructor
        )
        return enumDescriptor
    }


    private fun translateTopLevelModel(
        topLevelModel: TopLevelModel,
        parent: PackageFragmentDescriptorImpl
    ): DeclarationDescriptor? {
        return when (topLevelModel) {
            is InterfaceModel -> translateClassLike(topLevelModel, parent, isTopLevel = true)
            is ClassModel -> translateClassLike(topLevelModel, parent, isTopLevel = true)
            is ObjectModel -> translateObject(topLevelModel, parent, isCompanion = false, isTopLevel = true)
            is FunctionModel -> translateFunction(topLevelModel, parent)
            is VariableModel -> translateVariable(topLevelModel, parent)
            is TypeAliasModel -> translateTypeAlias(topLevelModel, parent)
            is EnumModel -> translateEnum(topLevelModel, parent)
            else -> raiseConcern("untranslated top level declaration: $topLevelModel") {
                null
            }
        }
    }

    fun translateModule(
        moduleModel: ModuleModel,
        moduleDescriptor: ModuleDescriptorImpl
    ): PackageFragmentDescriptor {

        return object : PackageFragmentDescriptorImpl(
            moduleDescriptor, translatePackageName(moduleModel.name)
        ) {
            var scope: MemberScope? = null

            override fun getMemberScope(): MemberScope {
                return scope ?: {
                    context.registeredImports.clear()
                    context.registeredImports.addAll(moduleModel.imports.map { translateName(it.name.shiftRight()!!) })
                    context.currentPackageName = moduleModel.name
                    scope = MutableMemberScope(moduleModel.declarations.mapNotNull {
                        translateTopLevelModel(
                            it,
                            this
                        )
                    }.toMutableList())
                    scope!!
                }()
            }
        }
    }
}

private fun expandTypeAlias(
    context: DescriptorContext,
    key: KotlinType,
    typeParameters: List<TypeParameterDescriptor>,
    newTypeArguments: List<TypeProjection>
): TypeProjection {
    val otherAlias = context.getTypeAliasDescriptorByConstructor(key.constructor)
    val replacement = key.arguments.map { keyArgument ->
        if (keyArgument.isStarProjection) {
            keyArgument
        } else {
            val replacementProjection =
                newTypeArguments.getOrNull(typeParameters.indexOfLast { it.defaultType.constructor == keyArgument.type.constructor })
                    ?: expandTypeAlias(context, keyArgument.type, typeParameters, newTypeArguments)

            TypeProjectionImpl(
                keyArgument.projectionKind,
                replacementProjection.type.replaceAnnotations(keyArgument.type.annotations)
            )
        }
    }
    if (otherAlias != null) {
        return TypeProjectionImpl(
            AbbreviatedType(
                expandTypeAlias(
                    context,
                    otherAlias.underlyingType,
                    typeParameters + otherAlias.defaultType.constructor.parameters,
                    newTypeArguments + replacement
                ).type as SimpleType,
                otherAlias.defaultType.replace(replacement)
            )
        )
    }
    return key.replace(replacement).asTypeProjection()
}

private fun computeAllParents(name: FqName): List<FqName> {
    val allNames = mutableListOf<FqName>(FqName.ROOT)
    var parent = name
    while (parent != FqName.ROOT) {
        allNames.add(parent)
        parent = parent.parent()
    }
    return allNames
}

private fun addFakeOverrides(context: DescriptorContext, classDescriptor: ClassDescriptor) {
    if (!context.shouldBeResolved(classDescriptor)) {
        return
    }
    val parents = classDescriptor.typeConstructor.supertypes.map {
        it.constructor.declarationDescriptor!!
    }.toList()
    for (parent in parents) {
        if (context.shouldBeResolved(parent as ClassDescriptor)) {
            addFakeOverrides(context, parent)
        }
    }
    val delegatedMembers =
        classDescriptor.typeConstructor.supertypes.filter { context.isDelegated(classDescriptor, it) }.flatMap {
            val substitutor = TypeSubstitutor.create(
                object : TypeSubstitution() {
                    override fun get(key: KotlinType): TypeProjection? = if (it.arguments.isEmpty()) {
                        null
                    } else {
                        expandTypeAlias(
                            DescriptorContext(context.config),
                            key,
                            (it.constructor.declarationDescriptor as ClassDescriptor).declaredTypeParameters,
                            it.arguments
                        )
                    }
                }
            )
            val members = DescriptorUtils.getAllDescriptors(it.memberScope).filterIsInstance<CallableMemberDescriptor>()
                .map { member -> member.substitute(substitutor) as CallableMemberDescriptor }
            members.filter { member -> member.overriddenDescriptors.isEmpty() }.map { member ->
                val delegatedMember = member.newCopyBuilder()
                    .setOwner(classDescriptor)
                    .setDispatchReceiverParameter(classDescriptor.thisAsReceiverParameter)
                    .setModality(if (member.modality == Modality.ABSTRACT) Modality.OPEN else member.modality)
                    .setKind(CallableMemberDescriptor.Kind.DELEGATION)
                    .setCopyOverrides(true)
                    .build()!!
                delegatedMember.overriddenDescriptors += member
                delegatedMember
            }
        }
    (classDescriptor.unsubstitutedMemberScope as MutableMemberScope).addMembers(delegatedMembers.toList())
    val fakeOverrides =
        DescriptorResolverUtils.resolveOverridesForNonStaticMembers(
            classDescriptor.name,
            classDescriptor.typeConstructor.supertypes.flatMap {
                val substitutor = TypeSubstitutor.create(
                    object : TypeSubstitution() {
                        override fun get(key: KotlinType): TypeProjection? = if (it.arguments.isEmpty()) {
                            null
                        } else {
                            expandTypeAlias(
                                DescriptorContext(context.config),
                                key,
                                (it.constructor.declarationDescriptor as ClassDescriptor).declaredTypeParameters,
                                it.arguments
                            )
                        }
                    }
                )
                DescriptorUtils.getAllDescriptors(it.memberScope).filterIsInstance<CallableMemberDescriptor>()
                    .map { member -> member.substitute(substitutor) }
            }.filterIsInstance<CallableMemberDescriptor>(),
            DescriptorUtils.getAllDescriptors(classDescriptor.unsubstitutedMemberScope)
                .filterIsInstance<CallableMemberDescriptor>(),
            classDescriptor,
            ErrorReporter.DO_NOTHING,
            (KotlinTypeChecker.DEFAULT as NewKotlinTypeChecker).overridingUtil
        )
    (classDescriptor.unsubstitutedMemberScope as MutableMemberScope).addMembers(fakeOverrides.toList())
    context.addResolved(classDescriptor)
}

fun SourceSetModel.translateToDescriptors(): ModuleDescriptor {

    val moduleDescriptor = ModuleDescriptorImpl(
        Name.special("<main>"),
        LockBasedStorageManager.NO_LOCKS,
        DefaultBuiltIns.Instance
    )

    val context = DescriptorContext(generateJSConfig())
    val translator = DescriptorTranslator(context)

    val fragments = sources.map {
        translator.translateModule(
            it.root,
            moduleDescriptor
        )
    }

    val namesToAdd = fragments.flatMap { computeAllParents(it.fqName) }.distinct() - fragments.map { it.fqName }

    val provider =
        PackageFragmentProviderImpl(fragments + namesToAdd.map { EmptyPackageFragmentDescriptor(moduleDescriptor, it) })

    moduleDescriptor.setDependencies(
        ModuleDependenciesImpl(
            listOf(moduleDescriptor) + builtIns.builtInsModule,
            setOf(),
            listOf()
        )
    )
    moduleDescriptor.initialize(provider)
    sources.forEach { sourceFile ->
        moduleDescriptor.getPackage(translatePackageName(sourceFile.root.name))
            .fragments.forEach { it.getMemberScope() }
    }

    context.initializeConstraints()

    context.getAllClassDescriptors().forEach { addFakeOverrides(context, it) }

    return moduleDescriptor
}