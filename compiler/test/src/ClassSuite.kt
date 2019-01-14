import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        ClassConventionsTests::class,
        ClassConstructorTests::class,
        ClassGenericTests::class,
        ClassMethodsTests::class,
        ClassVariablesTests::class
)
class ClassSuite