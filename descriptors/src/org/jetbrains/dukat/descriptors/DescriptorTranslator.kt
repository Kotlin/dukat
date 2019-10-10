package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.astModel.TypeModel
import org.jetbrains.dukat.astModel.TypeValueModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.kotlin.backend.common.SimpleMemberScope
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
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
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ClassConstructorDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PropertyDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
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

    private fun translateType(typeModel: TypeModel): KotlinType {
        if (typeModel is TypeValueModel) {
            return when (typeModel.value.translate()) {
                "Any" -> builtIns.anyType
                "Array" -> builtIns.getArrayType(
                    when (typeModel.params.first().variance) {
                        org.jetbrains.dukat.astModel.Variance.INVARIANT -> Variance.INVARIANT
                        org.jetbrains.dukat.astModel.Variance.COVARIANT -> Variance.OUT_VARIANCE
                        org.jetbrains.dukat.astModel.Variance.CONTRAVARIANT -> Variance.IN_VARIANCE
                    }, translateType(
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
                        ?: ErrorUtils.createErrorType("NOT_IMPLEMENTED")
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

    private fun translateInterface(
        interfaceModel: InterfaceModel,
        parent: PackageFragmentDescriptor
    ): ClassDescriptor {
        val companionDescriptor = interfaceModel.companionObject?.let {
            translateObject(it, parent, isCompanion = true)
        }
        val interfaceDescriptor = CustomClassDescriptor(
            parent = parent,
            name = interfaceModel.name,
            modality = Modality.ABSTRACT,
            classKind = ClassKind.INTERFACE,
            parentTypes = interfaceModel.parentEntities.map { translateType(it.value) },
            isCompanion = false,
            companionObject = companionDescriptor
        )
        context.registerDescriptor(interfaceModel.name, interfaceDescriptor)
        interfaceDescriptor.initialize(
            SimpleMemberScope((interfaceModel.members.mapNotNull {
                when (it) {
                    is MethodModel -> translateMethod(
                        it,
                        interfaceDescriptor
                    )
                    is PropertyModel -> translateProperty(
                        it,
                        interfaceDescriptor
                    )
                    else -> null
                }
            } + companionDescriptor as DeclarationDescriptor?).filterNotNull()),
            setOf(),
            null
        )
        return interfaceDescriptor
    }

    private fun translateClass(
        classModel: ClassModel,
        parent: PackageFragmentDescriptor
    ): ClassDescriptor {
        val companionDescriptor = classModel.companionObject?.let {
            translateObject(it, parent, isCompanion = true)
        }
        val classDescriptor = CustomClassDescriptor(
            parent = parent,
            name = classModel.name,
            modality = if (classModel.abstract) Modality.ABSTRACT else Modality.OPEN,
            classKind = ClassKind.CLASS,
            parentTypes = classModel.parentEntities.map { translateType(it.value) },
            isCompanion = false,
            companionObject = companionDescriptor
        )
        context.registerDescriptor(classModel.name, classDescriptor)
        val constructorModels = classModel.members.filterIsInstance<ConstructorModel>()
        val primaryConstructorDescriptor = if (classModel.primaryConstructor == null && constructorModels.isEmpty()) {
            translateConstructor(ConstructorModel(listOf(), listOf()), classDescriptor, true, Visibilities.PUBLIC)
        } else {
            classModel.primaryConstructor?.let { translateConstructor(it, classDescriptor, true, Visibilities.PUBLIC) }
        }
        val constructorDescriptors = (constructorModels.map {
            translateConstructor(
                it,
                classDescriptor,
                false,
                Visibilities.PUBLIC
            )
        } + primaryConstructorDescriptor).filterNotNull().toSet()

        classDescriptor.initialize(
            SimpleMemberScope((classModel.members.mapNotNull {
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
        constructorDescriptors.forEach {
            (it as ClassConstructorDescriptorImpl).returnType = classDescriptor.defaultType
        }
        return classDescriptor
    }

    private fun translateObject(
        objectModel: ObjectModel,
        parent: PackageFragmentDescriptor,
        isCompanion: Boolean
    ): ClassDescriptor {
        val objectDescriptor = CustomClassDescriptor(
            parent = parent,
            name = objectModel.name,
            modality = Modality.FINAL,
            classKind = ClassKind.OBJECT,
            parentTypes = objectModel.parentEntities.map { translateType(it.value) },
            isCompanion = isCompanion,
            companionObject = null
        )
        context.registerDescriptor(objectModel.name, objectDescriptor)
        val privatePrimaryConstructor = translateConstructor(
            ConstructorModel(listOf(), listOf()),
            objectDescriptor,
            true,
            Visibilities.PRIVATE
        )

        objectDescriptor.initialize(
            SimpleMemberScope(objectModel.members.mapNotNull {
                when (it) {
                    is MethodModel -> translateMethod(
                        it,
                        objectDescriptor
                    )
                    is PropertyModel -> translateProperty(
                        it,
                        objectDescriptor
                    )
                    else -> null
                }
            }),
            setOf(privatePrimaryConstructor),
            privatePrimaryConstructor
        )
        (privatePrimaryConstructor as ClassConstructorDescriptorImpl).returnType = objectDescriptor.defaultType
        return objectDescriptor
    }

    private fun translateTopLevelModel(
        topLevelModel: TopLevelModel,
        parent: PackageFragmentDescriptorImpl
    ): DeclarationDescriptor? {
        return when (topLevelModel) {
            is InterfaceModel -> translateInterface(topLevelModel, parent)
            is ClassModel -> translateClass(topLevelModel, parent)
            is ObjectModel -> translateObject(topLevelModel, parent, isCompanion = false)
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

fun SourceBundleModel.translateToDescriptors(): ModuleDescriptor {
    return DescriptorTranslator(DescriptorContext()).translateModule(sources.first().sources.first().root)
}