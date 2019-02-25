package org.jetbrains.dukat.compiler.tests.objectType

import org.jetbrains.dukat.compiler.tests.StandardTests
import org.junit.Test

class ObjectTypeTests : StandardTests() {

    @Test
    fun asTypeAlias() {
        assertContentEquals("objectType/asTypeAlias")
    }

    @Test
    fun asTypeAliasInNamespaces() {
        assertContentEquals("objectType/asTypeAliasInNamespaces")
    }

    @Test
    fun constructorParameter() {
        assertContentEquals("objectType/constructorParameter")
    }

    @Test
    fun emptyObjectTypeAsAny() {
        assertContentEquals("objectType/emptyObjectTypeAsAny")
    }

    @Test
    fun function() {
        assertContentEquals("objectType/function")
    }

    @Test
    fun functionIntersectionParameter() {
        assertContentEquals("objectType/functionIntersectionParameter")
    }

    @Test
    fun functionTypedIntersectionParameter() {
        assertContentEquals("objectType/functionTypedIntersectionParameter")
    }

    @Test
    fun generics() {
        assertContentEquals("objectType/functionTypedIntersectionParameter")
    }

    @Test
    fun genericViaTypeAlias() {
        assertContentEquals("objectType/functionTypedIntersectionParameter")
    }

    @Test
    fun inClassMembers() {
        assertContentEquals("objectType/inClassMembers")
    }

    @Test
    fun inInterface() {
        assertContentEquals("objectType/inInterface")
    }

    @Test
    fun inModuleMembers() {
        assertContentEquals("objectType/inModuleMembers")
    }

    @Test
    fun shareTempTypesBetweenModules() {
        assertContentEquals("objectType/shareTempTypesBetweenModules")
    }

    @Test
    fun useExportedType() {
        assertContentEquals("objectType/useExportedType")
    }

    @Test
    fun useJsonWhenPossible() {
        assertContentEquals("objectType/useJsonWhenPossible")
    }

    @Test
    fun useOneTraitForSameObjectTypes() {
        assertContentEquals("objectType/useOneTraitForSameObjectTypes")
    }

    @Test
    fun varrr() {
        assertContentEquals("objectType/var")
    }
}