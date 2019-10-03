package org.jetbrains.dukat.descriptors

import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.SourceBundleModel
import org.jetbrains.dukat.astModel.TopLevelModel
import org.jetbrains.dukat.panic.raiseConcern
import org.jetbrains.dukat.translatorString.translate
import org.jetbrains.kotlin.backend.common.SimpleMemberScope
import org.jetbrains.kotlin.builtins.DefaultBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProviderImpl
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.impl.ClassDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDependenciesImpl
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.KotlinType

private class DescriptorTranslator {
    private fun translateInterface(interfaceModel: InterfaceModel, parent: PackageFragmentDescriptorImpl): ClassDescriptor {
        val interfaceDescriptor = ClassDescriptorImpl(
            parent,
            Name.identifier(interfaceModel.name.translate()),
            Modality.ABSTRACT,
            ClassKind.INTERFACE,
            listOf<KotlinType>(),
            SourceElement.NO_SOURCE,
            true,
            LockBasedStorageManager.NO_LOCKS
        )
        interfaceDescriptor.initialize(
            SimpleMemberScope(listOf()),
            setOf(),
            null
        )
        return interfaceDescriptor
    }

    private fun translateTopLevelModel(topLevelModel: TopLevelModel, parent: PackageFragmentDescriptorImpl): DeclarationDescriptor? {
        return when (topLevelModel) {
            is InterfaceModel -> translateInterface(topLevelModel, parent)
            else -> raiseConcern("untranslated top level declaration: $topLevelModel") { null }
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
    return DescriptorTranslator().translateModule(sources.first().sources.first().root)
}