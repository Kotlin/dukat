package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astCommon.IdentifierEntity
import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceSetModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeParameterModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.kotlin.backend.common.SimpleMemberScope
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProviderImpl
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassConstructorDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ValueParameterDescriptorImpl
import org.jetbrains.kotlin.js.resolve.JsPlatformAnalyzerServices.builtIns
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.DescriptorFactory
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.LazyWrappedType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.createDynamicType

private class DescriptorTranslator(val context: DescriptorContext) {

    private fun translateVariance(variance: org.jetbrains.dukat.astModel.Variance): Variance {
        return when (variance) {
            org.jetbrains.dukat.astModel.Variance.INVARIANT -> Variance.INVARIANT
            org.jetbrains.dukat.astModel.Variance.COVARIANT -> Variance.OUT_VARIANCE
            org.jetbrains.dukat.astModel.Variance.CONTRAVARIANT -> Variance.IN_VARIANCE
        }
    }

    private fun translateType(typeModel: TypeModel): KotlinType {
        if (typeModel is TypeValueModel) {
            return context.getTypeParameter(typeModel.value)?.defaultType
                ?: when (typeModel.value.translate()) {
                    "Any" -> builtIns.anyType
                    "Array" -> builtIns.getArrayType(
                        translateVariance(typeModel.params.first().variance), translateType(
                            typeModel.params.first().type
                        )
                    )
                    "Boolean" -> builtIns.booleanType
                    "Byte" -> builtIns.byteType
                    "Char" -> builtIns.charType
                    "Double" -> builtIns.doubleType
                    "dynamic" -> createDynamicType(builtIns)
                    "Float" -> builtIns.floatType
                    "Int" -> builtIns.intType
                    "Long" -> builtIns.longType
                    "Nothing" -> builtIns.nothingType
                    "Number" -> builtIns.numberType
                    "Short" -> builtIns.shortType
                    "String" -> builtIns.stringType
                    "Unit" -> builtIns.unitType
                    else -> return LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                        context.getDescriptor(typeModel.value)?.defaultType?.makeNullableAsSpecified(typeModel.nullable)
                            ?: ErrorUtils.createErrorType(typeModel.value.translate())
                    }
                }.makeNullableAsSpecified(typeModel.nullable)
        }
        return ErrorUtils.createErrorType("NOT_IMPLEMENTED")
    }

    private fun translateParameters(
        parameterModels: List<ParameterModel>,
        parent: FunctionDescriptor
    ): List<ValueParameterDescriptorImpl> {
        return parameterModels.mapIndexed { index, parameter ->
            val type = translateType(parameter.type)
            val outType = if (parameter.vararg) {
                builtIns.getPrimitiveArrayKotlinTypeByPrimitiveKotlinType(
                    type
                ) ?: builtIns.getArrayType(Variance.OUT_VARIANCE, type)
            } else {
                type
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
                name = Name.identifier(parameter.name),
                outType = outType,
                declaresDefaultValue = parameter.optional || parameter.initializer != null,
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
        return parameters.mapIndexed { index, parameter ->
            TypeParameterDescriptorImpl.createWithDefaultBound(
                parent,
                Annotations.EMPTY,
                false,
                translateVariance(parameter.variance),
                Name.identifier(parameter.type.translate()),
                index
            )
        }
    }

    private fun translateMethod(methodModel: MethodModel, parent: ClassDescriptor): FunctionDescriptor {
        val functionDescriptor = SimpleFunctionDescriptorImpl.create(
            parent,
            Annotations.EMPTY,
            Name.identifier(methodModel.name.translate()),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE
        )
        functionDescriptor.initialize(
            null,
            null,
            emptyList(),
            translateParameters(methodModel.parameters, functionDescriptor),
            translateType(methodModel.type),
            when (parent.kind) {
                ClassKind.INTERFACE -> Modality.ABSTRACT
                else -> Modality.FINAL
            },
            Visibilities.PUBLIC
        )
        functionDescriptor.isOperator = methodModel.operator
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
        val propertyDescriptor = PropertyDescriptorImpl.create(
            parent,
            Annotations.EMPTY,
            when {
                parent.kind == ClassKind.INTERFACE -> Modality.OPEN
                propertyModel.open -> Modality.OPEN
                else -> Modality.FINAL
            },
            Visibilities.PUBLIC,
            true,
            Name.identifier(propertyModel.name.translate()),
            CallableMemberDescriptor.Kind.DECLARATION,
            SourceElement.NO_SOURCE,
            false,
            false,
            false,
            false,
            false,
            false
        )
        propertyDescriptor.setType(
            translateType(propertyModel.type),
            listOf(),
            null,
            null
        )
        val getter = DescriptorFactory.createDefaultGetter(
            propertyDescriptor,
            Annotations.EMPTY
        )
        getter.initialize(propertyDescriptor.type)
        val setter = DescriptorFactory.createDefaultSetter(
            propertyDescriptor,
            Annotations.EMPTY,
            Annotations.EMPTY
        )
        propertyDescriptor.initialize(
            getter,
            setter
        )
        return propertyDescriptor
    }

    private fun translateClassLike(
        classLikeModel: ClassLikeModel,
        parent: PackageFragmentDescriptor,
        isCompanion: Boolean = false
    ): ClassDescriptor {
        val companionDescriptor = classLikeModel.companionObject?.let {
            translateClassLike(it, parent, isCompanion = true)
        }

        var typeParameters: List<TypeParameterDescriptor> = listOf()
        if (classLikeModel is ClassModel) {
            typeParameters = translateTypeParameters(classLikeModel.typeParameters, parent)
        }
        if (classLikeModel is InterfaceModel) {
            typeParameters = translateTypeParameters(classLikeModel.typeParameters, parent)
        }
        typeParameters.forEach {
            context.registerTypeParameter(IdentifierEntity(it.name.identifier), it)
        }

        val classDescriptor = CustomClassDescriptor(
            parent = parent,
            name = classLikeModel.name,
            modality = when (classLikeModel) {
                is ClassModel -> if (classLikeModel.abstract) Modality.ABSTRACT else Modality.OPEN
                is InterfaceModel -> Modality.ABSTRACT
                is ObjectModel -> Modality.FINAL
                else -> Modality.FINAL
            },
            classKind = when (classLikeModel) {
                is ClassModel -> ClassKind.CLASS
                is InterfaceModel -> ClassKind.INTERFACE
                is ObjectModel -> ClassKind.OBJECT
                else -> ClassKind.CLASS
            },
            parentTypes = classLikeModel.parentEntities.map { translateType(it.value) },
            isCompanion = isCompanion,
            companionObject = companionDescriptor,
            typeParameters = typeParameters
        )
        context.registerDescriptor(classLikeModel.name, classDescriptor)

        var primaryConstructorDescriptor: ClassConstructorDescriptor? = null
        var constructorDescriptors: Set<ClassConstructorDescriptor> = setOf()
        if (classLikeModel is ObjectModel) {
            primaryConstructorDescriptor = translateConstructor(
                ConstructorModel(listOf(), listOf()),
                classDescriptor,
                true,
                Visibilities.PRIVATE
            )
            constructorDescriptors = setOf(primaryConstructorDescriptor)
        }
        if (classLikeModel is ClassModel) {
            context.registerDescriptor(classLikeModel.name, classDescriptor)
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
            SimpleMemberScope((classLikeModel.members.mapNotNull
            {
                when (it) {
                    is MethodModel -> translateMethod(
                        it,
                        classDescriptor
                    )
                    is PropertyModel -> translateProperty(
                        it,
                        classDescriptor
                    )
                    else -> null
                }
            } + companionDescriptor as DeclarationDescriptor?).filterNotNull()),
            constructorDescriptors,
            primaryConstructorDescriptor
        )

        typeParameters.forEach {
            context.removeTypeParameter(IdentifierEntity(it.name.identifier))
        }

        return classDescriptor
    }

    private fun translateTopLevelModel(
        topLevelModel: TopLevelModel,
        parent: PackageFragmentDescriptorImpl
    ): DeclarationDescriptor? {
        println(topLevelModel)
        return when (topLevelModel) {
            is InterfaceModel -> translateClassLike(topLevelModel, parent)
            is ClassModel -> translateClassLike(topLevelModel, parent)
            is ObjectModel -> translateClassLike(topLevelModel, parent)
            else -> raiseConcern("untranslated top level declaration: $topLevelModel") {
                null
            }
        }
    }

    fun translateModule(moduleModel: ModuleModel): ModuleDescriptor {
        val moduleDescriptor = ModuleDescriptorImpl(
            Name.special("<main>"),
            LockBasedStorageManager.NO_LOCKS,
            DefaultBuiltIns.Instance
        )

        val fragmentDescriptor = object : PackageFragmentDescriptorImpl(
            moduleDescriptor, FqName("")
        ) {
            override fun getMemberScope() =
                SimpleMemberScope(moduleModel.declarations.mapNotNull { translateTopLevelModel(it, this) })
        }

        val provider = PackageFragmentProviderImpl(listOf(fragmentDescriptor))
        moduleDescriptor.setDependencies(ModuleDependenciesImpl(listOf(moduleDescriptor), setOf(), listOf()))
        moduleDescriptor.initialize(provider)

        return moduleDescriptor
    }
}

fun SourceSetModel.translateToDescriptors(): ModuleDescriptor {
    return DescriptorTranslator(DescriptorContext()).translateModule(sources.first().root)
}