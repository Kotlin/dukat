package org.jetbrains.dukat.compiler.tests.descriptors

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.google.common.collect.Lists
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError

import java.io.PrintStream

object DescriptorValidator {

    fun validate(validationStrategy: ValidationVisitor, descriptor: DeclarationDescriptor) {
        val collector = DiagnosticCollectorForTests()
        validate(validationStrategy, descriptor, collector)
        collector.done()
    }

    fun validate(
        validator: ValidationVisitor,
        descriptor: DeclarationDescriptor,
        collector: DiagnosticCollector
    ) {
        RecursiveDescriptorProcessor.process(descriptor, collector, validator)
    }

    private fun report(collector: DiagnosticCollector, descriptor: DeclarationDescriptor, message: String) {
        collector.report(ValidationDiagnostic(descriptor, message))
    }

    interface DiagnosticCollector {
        fun report(diagnostic: ValidationDiagnostic)
    }

    class ValidationVisitor private constructor() : DeclarationDescriptorVisitor<Boolean, DiagnosticCollector> {

        private var allowErrorTypes = false
        private var recursiveFilter = { _: DeclarationDescriptor -> true }

        fun allowErrorTypes(): ValidationVisitor {
            this.allowErrorTypes = true
            return this
        }

        private fun validateScope(
            scope: MemberScope,
            collector: DiagnosticCollector
        ) {
            for (descriptor in DescriptorUtils.getAllDescriptors(scope)) {
                if (recursiveFilter(descriptor)) {
                    descriptor.accept(ScopeValidatorVisitor(collector), scope)
                }
            }
        }

        private fun validateType(
            descriptor: DeclarationDescriptor,
            type: KotlinType?,
            collector: DiagnosticCollector
        ) {
            if (type == null) {
                report(collector, descriptor, "No type")
                return
            }

            if (!allowErrorTypes && type.isError) {
                report(collector, descriptor, "Error type: $type")
                return
            }

            validateScope(type.memberScope, collector)
        }

        private fun validateReturnType(descriptor: CallableDescriptor, collector: DiagnosticCollector) {
            validateType(descriptor, descriptor.returnType, collector)
        }

        private fun validateTypes(
            descriptor: DeclarationDescriptor,
            collector: DiagnosticCollector,
            types: Collection<KotlinType>
        ) {
            for (type in types) {
                validateType(descriptor, type, collector)
            }
        }

        private fun validateCallable(descriptor: CallableDescriptor, collector: DiagnosticCollector) {
            validateReturnType(descriptor, collector)
            validateTypeParameters(collector, descriptor.typeParameters)
            validateValueParameters(collector, descriptor.valueParameters)
        }

        override fun visitPackageFragmentDescriptor(
            descriptor: PackageFragmentDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateScope(descriptor.getMemberScope(), collector)
            return true
        }

        override fun visitPackageViewDescriptor(
            descriptor: PackageViewDescriptor,
            collector: DiagnosticCollector
        ): Boolean? {
            if (!recursiveFilter(descriptor)) return false

            validateScope(descriptor.memberScope, collector)
            return true
        }

        override fun visitVariableDescriptor(
            descriptor: VariableDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateReturnType(descriptor, collector)
            return true
        }

        override fun visitFunctionDescriptor(
            descriptor: FunctionDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateCallable(descriptor, collector)
            return true
        }

        override fun visitTypeParameterDescriptor(
            descriptor: TypeParameterDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateTypes(descriptor, collector, descriptor.upperBounds)

            validateType(descriptor, descriptor.defaultType, collector)

            return true
        }

        override fun visitClassDescriptor(
            descriptor: ClassDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateTypeParameters(collector, descriptor.declaredTypeParameters)

            val supertypes = descriptor.typeConstructor.supertypes
            if (supertypes.isEmpty() && descriptor.kind != ClassKind.INTERFACE
                && !KotlinBuiltIns.isSpecialClassWithNoSupertypes(descriptor)
            ) {
                report(collector, descriptor, "No supertypes for non-trait")
            }
            validateTypes(descriptor, collector, supertypes)

            validateType(descriptor, descriptor.defaultType, collector)

            validateScope(descriptor.unsubstitutedInnerClassesScope, collector)

            val primary = Lists.newArrayList<ConstructorDescriptor>()
            for (constructorDescriptor in descriptor.constructors) {
                if (constructorDescriptor.isPrimary) {
                    primary.add(constructorDescriptor)
                }
            }
            if (primary.size > 1) {
                report(collector, descriptor, "Many primary constructors: $primary")
            }

            val primaryConstructor = descriptor.unsubstitutedPrimaryConstructor
            if (primaryConstructor != null) {
                if (!descriptor.constructors.contains(primaryConstructor)) {
                    report(
                        collector, primaryConstructor,
                        "Primary constructor not in getConstructors() result: " + descriptor.constructors
                    )
                }
            }

            val companionObjectDescriptor = descriptor.companionObjectDescriptor
            if (companionObjectDescriptor != null && !companionObjectDescriptor.isCompanionObject) {
                report(collector, companionObjectDescriptor, "Companion object should be marked as such")
            }

            return true
        }

        override fun visitTypeAliasDescriptor(
            descriptor: TypeAliasDescriptor, data: DiagnosticCollector
        ): Boolean? {
            // TODO typealias
            return true
        }

        override fun visitModuleDeclaration(
            descriptor: ModuleDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            return true
        }

        override fun visitConstructorDescriptor(
            constructorDescriptor: ConstructorDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            visitFunctionDescriptor(constructorDescriptor, collector)

            assertEqualTypes(
                constructorDescriptor, collector,
                "return type",
                constructorDescriptor.containingDeclaration.defaultType,
                constructorDescriptor.returnType
            )

            return true
        }

        override fun visitScriptDescriptor(
            scriptDescriptor: ScriptDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            return true
        }

        override fun visitPropertyDescriptor(
            descriptor: PropertyDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateCallable(descriptor, collector)

            val getter = descriptor.getter
            if (getter != null) {
                assertEqualTypes(
                    getter,
                    collector,
                    "getter return type",
                    descriptor.type,
                    getter.returnType!!
                )
                validateAccessor(descriptor, collector, getter)
            }

            val setter = descriptor.setter
            if (setter != null) {
                assertEquals(setter, collector, "setter parameter count", 1, setter.valueParameters.size)
                assertEqualTypes(
                    setter,
                    collector,
                    "setter parameter type",
                    descriptor.type,
                    setter.valueParameters[0].type
                )
                assertEquals(
                    setter,
                    collector,
                    "corresponding property",
                    descriptor,
                    setter.correspondingProperty
                )
            }

            return true
        }

        override fun visitValueParameterDescriptor(
            descriptor: ValueParameterDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            return visitVariableDescriptor(descriptor, collector)
        }

        override fun visitPropertyGetterDescriptor(
            descriptor: PropertyGetterDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            return visitFunctionDescriptor(descriptor, collector)
        }

        override fun visitPropertySetterDescriptor(
            descriptor: PropertySetterDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            return visitFunctionDescriptor(descriptor, collector)
        }

        override fun visitReceiverParameterDescriptor(
            descriptor: ReceiverParameterDescriptor, collector: DiagnosticCollector
        ): Boolean? {
            validateType(descriptor, descriptor.type, collector)

            return true
        }

        companion object {
            fun errorTypesForbidden(): ValidationVisitor {
                return ValidationVisitor()
            }

            fun errorTypesAllowed(): ValidationVisitor {
                return ValidationVisitor().allowErrorTypes()
            }

            private fun validateTypeParameters(
                collector: DiagnosticCollector,
                parameters: List<TypeParameterDescriptor>
            ) {
                for (i in parameters.indices) {
                    val typeParameterDescriptor = parameters[i]
                    if (typeParameterDescriptor.index != i) {
                        report(
                            collector,
                            typeParameterDescriptor,
                            "Incorrect index: " + typeParameterDescriptor.index + " but must be " + i
                        )
                    }
                }
            }

            private fun validateValueParameters(
                collector: DiagnosticCollector,
                parameters: List<ValueParameterDescriptor>
            ) {
                for (i in parameters.indices) {
                    val valueParameterDescriptor = parameters[i]
                    if (valueParameterDescriptor.index != i) {
                        report(
                            collector,
                            valueParameterDescriptor,
                            "Incorrect index: " + valueParameterDescriptor.index + " but must be " + i
                        )
                    }
                }
            }

            private fun <T> assertEquals(
                descriptor: DeclarationDescriptor,
                collector: DiagnosticCollector,
                name: String,
                expected: T,
                actual: T
            ) {
                if (expected != actual) {
                    report(collector, descriptor, "Wrong $name: $actual must be $expected")
                }
            }

            private fun assertEqualTypes(
                descriptor: DeclarationDescriptor,
                collector: DiagnosticCollector,
                name: String,
                expected: KotlinType,
                actual: KotlinType
            ) {
                if (expected.isError && actual.isError) {
                    assertEquals(descriptor, collector, name, expected.toString(), actual.toString())
                } else if (expected != actual) {
                    report(collector, descriptor, "Wrong $name: $actual must be $expected")
                }
            }

            private fun validateAccessor(
                descriptor: PropertyDescriptor,
                collector: DiagnosticCollector,
                accessor: PropertyAccessorDescriptor
            ) {
                // TODO: fix the discrepancies in descriptor construction and enable these checks
                //assertEquals(accessor, collector, name + " visibility", descriptor.getVisibility(), accessor.getVisibility());
                //assertEquals(accessor, collector, name + " modality", descriptor.getModality(), accessor.getModality());
                assertEquals(accessor, collector, "corresponding property", descriptor, accessor.correspondingProperty)
            }
        }

    }

    private class ScopeValidatorVisitor(private val collector: DiagnosticCollector) :
        DeclarationDescriptorVisitor<Void, MemberScope> {

        private fun report(expected: DeclarationDescriptor, message: String) {
            report(collector, expected, message)
        }

        private fun assertFound(
            scope: MemberScope,
            expected: DeclarationDescriptor,
            found: DeclarationDescriptor?
        ) {
            if (found == null) {
                report(expected, "Not found in $scope")
            }
            if (expected !== found) {
                report(expected, "Lookup error in $scope: $found")
            }
        }

        private fun assertFound(
            scope: MemberScope,
            expected: DeclarationDescriptor,
            found: Collection<DeclarationDescriptor>
        ) {
            if (!found.contains(expected)) {
                report(expected, "Not found in $scope: $found")
            }
        }

        override fun visitPackageFragmentDescriptor(
            descriptor: PackageFragmentDescriptor, scope: MemberScope
        ): Void? {
            return null
        }

        override fun visitPackageViewDescriptor(
            descriptor: PackageViewDescriptor, scope: MemberScope
        ): Void? {
            return null
        }

        override fun visitVariableDescriptor(
            descriptor: VariableDescriptor, scope: MemberScope
        ): Void? {
            assertFound(scope, descriptor, scope.getContributedVariables(descriptor.name, NoLookupLocation.FROM_TEST))
            return null
        }

        override fun visitFunctionDescriptor(
            descriptor: FunctionDescriptor, scope: MemberScope
        ): Void? {
            assertFound(scope, descriptor, scope.getContributedFunctions(descriptor.name, NoLookupLocation.FROM_TEST))
            return null
        }

        override fun visitTypeParameterDescriptor(
            descriptor: TypeParameterDescriptor, scope: MemberScope
        ): Void? {
            assertFound(
                scope,
                descriptor,
                scope.getContributedClassifier(descriptor.name, NoLookupLocation.FROM_TEST)
            )
            return null
        }

        override fun visitClassDescriptor(
            descriptor: ClassDescriptor, scope: MemberScope
        ): Void? {
            assertFound(
                scope,
                descriptor,
                scope.getContributedClassifier(descriptor.name, NoLookupLocation.FROM_TEST)
            )
            return null
        }

        override fun visitTypeAliasDescriptor(descriptor: TypeAliasDescriptor, data: MemberScope): Void? {
            // TODO typealias
            return null
        }

        override fun visitModuleDeclaration(
            descriptor: ModuleDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Module found in scope: $scope")
            return null
        }

        override fun visitConstructorDescriptor(
            descriptor: ConstructorDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Constructor found in scope: $scope")
            return null
        }

        override fun visitScriptDescriptor(
            descriptor: ScriptDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Script found in scope: $scope")
            return null
        }

        override fun visitPropertyDescriptor(
            descriptor: PropertyDescriptor, scope: MemberScope
        ): Void? {
            return visitVariableDescriptor(descriptor, scope)
        }

        override fun visitValueParameterDescriptor(
            descriptor: ValueParameterDescriptor, scope: MemberScope
        ): Void? {
            return visitVariableDescriptor(descriptor, scope)
        }

        override fun visitPropertyGetterDescriptor(
            descriptor: PropertyGetterDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Getter found in scope: $scope")
            return null
        }

        override fun visitPropertySetterDescriptor(
            descriptor: PropertySetterDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Setter found in scope: $scope")
            return null
        }

        override fun visitReceiverParameterDescriptor(
            descriptor: ReceiverParameterDescriptor, scope: MemberScope
        ): Void? {
            report(descriptor, "Receiver parameter found in scope: $scope")
            return null
        }
    }

    class ValidationDiagnostic(val descriptor: DeclarationDescriptor, val message: String) {
        private val stackTrace: Throwable = Throwable()

        fun printStackTrace(out: PrintStream) {
            out.println(descriptor)
            out.println(message)
            stackTrace.printStackTrace(out)
        }

        override fun toString(): String {
            return "$descriptor > $message"
        }
    }

    private class DiagnosticCollectorForTests : DiagnosticCollector {
        private var errorsFound = false

        override fun report(diagnostic: ValidationDiagnostic) {
            diagnostic.printStackTrace(System.err)
            errorsFound = true
        }

        fun done() {
            if (errorsFound) {
                println("Descriptor validation failed (see messages above)")
            }
        }
    }
}
