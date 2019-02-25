package org.jetbrains.dukat.compiler.tests

import ClassSuite
import InterfaceSuite
import org.jetbrains.dukat.compiler.tests.enum.EnumTests
import org.jetbrains.dukat.compiler.tests.new.NewTests
import org.jetbrains.dukat.compiler.tests.objectType.ObjectTypeTests
import org.jetbrains.dukat.compiler.tests.thisType.ThisTypeTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    InterfaceSuite::class,
    ClassSuite::class,
    TopLevelMembersSuite::class,
    ObjectTypeTests::class,
    ThisTypeTests::class,
    NewTests::class,
    EnumTests::class
)
class MinimalTestSuite