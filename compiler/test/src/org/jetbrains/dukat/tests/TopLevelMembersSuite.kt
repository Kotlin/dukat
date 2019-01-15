package org.jetbrains.dukat.tests

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        FunctionTests::class,
        GenericsTests::class,
        VariablesTests::class
)
class TopLevelMembersSuite