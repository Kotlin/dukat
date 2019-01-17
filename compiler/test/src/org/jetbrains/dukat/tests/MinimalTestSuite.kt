package org.jetbrains.dukat.tests

import ClassSuite
import InterfaceSuite
import org.jetbrains.dukat.tests.enum.EnumTests
import org.jetbrains.dukat.tests.new.NewTests
import org.jetbrains.dukat.tests.objectType.ObjectTypeTests
import org.jetbrains.dukat.tests.thisType.ThisTypeTests
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