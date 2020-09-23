package org.jetbrains.dukat.compiler.tests.descriptors

import org.jetbrains.kotlin.com.google.common.collect.ImmutableSet
import org.jetbrains.kotlin.com.google.common.collect.Lists
import org.jetbrains.kotlin.contracts.description.AbstractContractProvider
import org.jetbrains.kotlin.contracts.description.ContractDescriptionRenderer
import org.jetbrains.kotlin.contracts.description.ContractProviderKey
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassOrPackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.SubpackagesScope
import org.jetbrains.kotlin.platform.isCommon
import org.jetbrains.kotlin.renderer.AnnotationArgumentsRenderingPolicy
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.renderer.DescriptorRendererModifier
import org.jetbrains.kotlin.renderer.DescriptorRendererOptions
import org.jetbrains.kotlin.renderer.OverrideRenderingPolicy
import org.jetbrains.kotlin.renderer.PropertyAccessorRenderingPolicy
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.DescriptorUtils.isEnumEntry
import org.jetbrains.kotlin.resolve.MemberComparator
import org.jetbrains.kotlin.resolve.scopes.ChainedMemberScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.utils.Printer
import java.util.*
import java.util.function.Consumer

class RecursiveDescriptorComparator {
    private val conf: Configuration = RECURSIVE_ALL

    fun serializeRecursively(declarationDescriptor: DeclarationDescriptor): String {
        val result = StringBuilder()
        appendDeclarationRecursively(
                declarationDescriptor, DescriptorUtils.getContainingModule(declarationDescriptor),
                Printer(result, 1), true
        )
        return result.toString()
    }

    private fun appendDeclarationRecursively(
            descriptor: DeclarationDescriptor,
            module: ModuleDescriptor,
            printer: Printer,
            topLevel: Boolean
    ) {
        if (!isFromModule(descriptor, module)) return

        val isEnumEntry = isEnumEntry(descriptor)
        val isClassOrPackage =
                (descriptor is ClassOrPackageFragmentDescriptor || descriptor is PackageViewDescriptor) && !isEnumEntry

        val content = java.lang.StringBuilder()
        if (isClassOrPackage) {
            val child = Printer(content, printer)

            if (!topLevel) {
                child.pushIndent()
            }

            if (descriptor is ClassDescriptor) {
                appendSubDescriptors(
                        descriptor,
                        module,
                        descriptor.defaultType.memberScope,
                        descriptor.constructors,
                        child
                )
                val staticScope = descriptor.staticScope
                if (!DescriptorUtils.getAllDescriptors(staticScope).isEmpty()) {
                    child.println()
                    child.println("// Static members")
                    appendSubDescriptors(descriptor, module, staticScope, emptyList(), child)
                }
            } else if (descriptor is PackageFragmentDescriptor) {
                appendSubDescriptors(
                        descriptor, module,
                        descriptor.getMemberScope(),
                        emptyList(), child
                )
            } else if (descriptor is PackageViewDescriptor) {
                appendSubDescriptors(
                        descriptor, module,
                        getPackageScopeInModule(descriptor, module),
                        emptyList(), child
                )
            }

            if (!topLevel) {
                if (child.isEmpty && (descriptor is PackageFragmentDescriptor || descriptor is PackageViewDescriptor)) {
                    return
                }

                printer.println()
            }
        }

        printDescriptor(descriptor, printer)

        if (isClassOrPackage) {
            if (!topLevel) {
                printer.printlnWithNoIndent(" {")
                printer.printWithNoIndent(content)
                printer.println("}")
            } else {
                printer.println()
                printer.println()
                printer.printWithNoIndent(content.trimStart(*Printer.LINE_SEPARATOR.toCharArray()))
            }
        } else if (conf.checkPropertyAccessors && descriptor is PropertyDescriptor) {
            printer.printlnWithNoIndent()
            printer.pushIndent()

            descriptor.getter?.let { printer.println(conf.renderer.render(it)) }
            descriptor.setter?.let { printer.println(conf.renderer.render(it)) }

            printer.popIndent()
        } else {
            printer.printlnWithNoIndent()
        }

        if (isEnumEntry) {
            printer.println()
        }
    }

    private fun printDescriptor(
            descriptor: DeclarationDescriptor,
            printer: Printer
    ) {
        val isPrimaryConstructor = conf.checkPrimaryConstructors &&
                descriptor is ConstructorDescriptor && descriptor.isPrimary

        printer.print(if (isPrimaryConstructor) "/*primary*/ " else "", conf.renderer.render(descriptor))

        if (descriptor is FunctionDescriptor && conf.checkFunctionContracts) {
            printEffectsIfAny(
                    descriptor,
                    printer
            )
        }
    }

    private fun getPackageScopeInModule(descriptor: PackageViewDescriptor, module: ModuleDescriptor): MemberScope {
        // See LazyPackageViewDescriptorImpl#memberScope
        val scopes = ArrayList<MemberScope>()
        for (fragment in descriptor.fragments) {
            if (isFromModule(fragment, module)) {
                scopes.add(fragment.getMemberScope())
            }
        }
        scopes.add(SubpackagesScope(module, descriptor.fqName))
        return ChainedMemberScope.create("test", scopes)
    }

    private fun isFromModule(descriptor: DeclarationDescriptor, module: ModuleDescriptor): Boolean {
        if (conf.renderDeclarationsFromOtherModules) return true

        if (descriptor is PackageViewDescriptor) {
            // PackageViewDescriptor does not belong to any module, so we check if one of its containing fragments is in our module
            for (fragment in descriptor.fragments) {
                if (module == DescriptorUtils.getContainingModule(fragment)) return true
            }
        }

        // 'expected' declarations do not belong to the platform-specific module, even though they participate in the analysis
        return if (descriptor is MemberDescriptor && descriptor.isExpect &&
                !module.platform.isCommon()
        ) false else module == DescriptorUtils.getContainingModule(descriptor)

    }

    private fun shouldSkip(subDescriptor: DeclarationDescriptor): Boolean {
        val isFunctionFromAny = (subDescriptor.containingDeclaration is ClassDescriptor
                && subDescriptor is FunctionDescriptor
                && KOTLIN_ANY_METHOD_NAMES.contains(subDescriptor.getName().asString()))
        return isFunctionFromAny && !conf.includeMethodsOfKotlinAny || !conf.recursiveFilter(subDescriptor)
    }

    private fun appendSubDescriptors(
            descriptor: DeclarationDescriptor,
            module: ModuleDescriptor,
            memberScope: MemberScope,
            extraSubDescriptors: Collection<DeclarationDescriptor>,
            printer: Printer
    ) {
        if (!isFromModule(descriptor, module)) return

        val subDescriptors = Lists.newArrayList<DeclarationDescriptor>()

        subDescriptors.addAll(DescriptorUtils.getAllDescriptors(memberScope))
        subDescriptors.addAll(extraSubDescriptors)

        subDescriptors.sortWith(MemberComparator.INSTANCE)

        for (subDescriptor in subDescriptors) {
            if (!shouldSkip(subDescriptor)) {
                appendDeclarationRecursively(subDescriptor, module, printer, false)
            }
        }
    }

    class Configuration(
            val checkPrimaryConstructors: Boolean,
            val checkPropertyAccessors: Boolean,
            val includeMethodsOfKotlinAny: Boolean,
            val renderDeclarationsFromOtherModules: Boolean,
            val checkFunctionContracts: Boolean,
            val recursiveFilter: (DeclarationDescriptor) -> Boolean
    ) {
        val renderer: DescriptorRenderer = rendererWithPropertyAccessors(DEFAULT_RENDERER, checkPropertyAccessors)

        private fun rendererWithPropertyAccessors(
                renderer: DescriptorRenderer, checkPropertyAccessors: Boolean
        ): DescriptorRenderer {
            return newRenderer(
                    renderer
            ) { options ->
                options.propertyAccessorRenderingPolicy =
                        if (checkPropertyAccessors) PropertyAccessorRenderingPolicy.DEBUG else PropertyAccessorRenderingPolicy.NONE
            }
        }

        private fun newRenderer(
                renderer: DescriptorRenderer, configure: Consumer<DescriptorRendererOptions>
        ): DescriptorRenderer {
            return renderer.withOptions {
                configure.accept(this)
                Unit
            }
        }
    }

    companion object {

        private val DEFAULT_RENDERER = DescriptorRenderer.withOptions {
            withDefinedIn = false
            excludedAnnotationClasses = setOf()
            overrideRenderingPolicy = OverrideRenderingPolicy.RENDER_OPEN_OVERRIDE
            includePropertyConstant = true
            classifierNamePolicy = ClassifierNamePolicy.FULLY_QUALIFIED
            verbose = true
            annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
            modifiers = DescriptorRendererModifier.ALL
            Unit
        }

        val RECURSIVE_ALL = Configuration(
                checkPrimaryConstructors = true,
                checkPropertyAccessors = true,
                includeMethodsOfKotlinAny = true,
                renderDeclarationsFromOtherModules = false,
                checkFunctionContracts = true,
                recursiveFilter = { true }
        )

        private val KOTLIN_ANY_METHOD_NAMES = ImmutableSet.of("equals", "hashCode", "toString")

        private fun printEffectsIfAny(functionDescriptor: FunctionDescriptor, printer: Printer) {
            val contractProvider =
                    functionDescriptor.getUserData<AbstractContractProvider>(ContractProviderKey) ?: return

            val contractDescription = contractProvider.getContractDescription()
            if (contractDescription == null || contractDescription.effects.isEmpty()) return

            printer.println()
            printer.pushIndent()
            for (effect in contractDescription.effects) {
                val sb = StringBuilder()
                val renderer = ContractDescriptionRenderer(sb)
                effect.accept(renderer, Unit)
                printer.println(sb.toString())
            }
            printer.popIndent()
        }
    }
}
