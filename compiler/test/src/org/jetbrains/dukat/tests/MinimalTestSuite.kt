package org.jetbrains.dukat.tests

import ClassSuite
import InterfaceSuite
import org.jetbrains.dukat.tests.objectType.ObjectTypeTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    InterfaceSuite::class,
    ClassSuite::class,
    TopLevelMembersSuite::class,
    ObjectTypeTests::class
)
class MinimalTestSuite