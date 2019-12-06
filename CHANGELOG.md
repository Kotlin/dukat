# Changelog

### [0.0.24] - 05'December, 2019
 - [In some cases](https://github.com/Kotlin/dukat/commit/041883a44448d6a591b1a922a9fb5a313cf16f2b) @Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE") was missing.
 - Mutliple escaping issues fixes.
 - Rename params instead of escaping them (for instance, `object` renamed to just `obj`).
 - [Convert](https://github.com/Kotlin/dukat/commit/830041d502c633e0f70fa42e9e0cbe595bfb2adb) UnionType to string whenever it's possible. 
 - Resolve "import as" clauses and introduce imports accordingly.
 - [Resolve](https://github.com/Kotlin/dukat/commit/356d31872119a13426f4e38321041fec9803eb44) overrides for nested classes and interfaces.
 - Remove conflicting overloads.
 - [Treat](https://github.com/Kotlin/dukat/commit/7457cd692b67de7310285e6cd92f6265f8961001) names starting with dot as non-supportable. Unforunately, we need to introduce some changes in Kotlin/JS compiler itself for supporting property names not accessible via dot.
 - Always add JsNonModule alongside with JsModule.
 - Convert boolean literals to Boolean while converting types.
 - Copy methods generated from unrolled union types to ancestor classes.   

### [0.0.23] - 07'November, 2019
 - equals should have "override" modifier only when param is Any?
 - Resolve file names from namespaced nodejs packages.
 - Correct override resolving for methods with return type.
 - Resolve overrides when return type is generic.
 - Replace ReadonlyArray from ts stdlib with just Array.
 - Preserve TypeParams in unaliased entities in cases when they were lost.
 - Replace entity inherited from a final class (in a Kotlin stdlib sense with alias.

### [0.0.22] - 31'October, 2019
 - Better support for Partial. Whenever this class is defined in this particular declaration source set,
   new class is generated which mimicks Partial behaviour.
 - Resolving relative module names (see [#110](https://github.com/Kotlin/dukat/issues/110) - Inconsistent naming while translating @types/lodash)
 - Support type references in typescript declarations.     

### [0.0.21] - 22'October, 2019
 - [#133](https://github.com/Kotlin/dukat/issues/133) Lack of type params in generated interfaces
 - [#136](https://github.com/Kotlin/dukat/issues/136) Convert nested nullable unions correctly
 - [#141](https://github.com/Kotlin/dukat/issues/141) ThisTypeDeclaration leak in inline functions generated from merged interfaces
 - Default ts library is switched to lib.es6.d.ts. 

### [0.0.20] - 10'October, 2019
 With this release idl target is no longer experimental. It's main focus remains the same, however: 
 to generate stdlib for following releases of Kotlin/JS. 
 
 - [idl] support for record types, partial namespaces
 - [idl] minor improvements for mixins and enums
 - [idl] ([#124](https://github.com/Kotlin/dukat/issues/124)) always show public visibility modifier in accordance with [KEEP-45](https://github.com/Kotlin/KEEP/issues/45) 
 - Translate Pick<T, K> to Any (in case T is actually a type param)
 - [#129 - Failing to translate generic params with default values when such values are not type params](https://github.com/Kotlin/dukat/issues/129)

#### [0.0.19] - 30'September, 2019
 - [Namespaced aliases lost in translation](https://github.com/Kotlin/dukat/issues/119)
 - Correct escaping for entities in heritage clauses, constraint params
 - Any toString method with no parameters is override
 - [Generic params information is lost for lambdas in types](https://github.com/Kotlin/dukat/issues/118)
   Support for generic params with default values pointing to some other generic params
 

#### [0.0.18] - 20'September, 2019
Starting from this release generated files names will
match following pattern:

 **[file_name].[packageName]?.[module_name]?.[additional_postfix]**

For instance, let's say we have file `foo.d.ts` in `fooController` package,
and following file is generated: `foo.helpers.module_fooController.kt`.
Then we know that the original file was called foo.d.ts, the file itself
belongs to the package "helpers" and npm module is fooController.

 - Module name resolving is based only on existence of package.json and its content (DUKAT-1 and DUKAT-4)
 - [Losing part of generated files because of the name clash](https://github.com/Kotlin/dukat/issues/109)
 - [Generated entities name clash, case-sensitivity related](https://github.com/Kotlin/dukat/issues/114)
 - [regression] -r (build report) command-line flag behaviour fixed
 - Batch mode removed completely - it was used for  dev purposes only and in it's current form proved to be quite inefficient

#### [0.0.17] - 16'September, 2019
 - This release contains mainly idl-related changes. The idl target is considered production-ready,
  however its support is still experimental.   

#### [0.0.16] - 16'August, 2019 
 - Compiling protobuf-generated java code to 1.6 (which is currently the target version for dukat)
#### [0.0.15] - 16'August, 2019
 - The only change is speed, however, this change is big. Since this release ts is parsed on nodejs side and
 passed in a binary form to the JVM side. 

#### [0.0.14] - 8'August, 2019
 - Add global module annotation in case we have default export but don't have module annotation so far
 - [idl] Allow translation of several files (dependent on each other)
 - [idl] Introduce getters/setters and enums
 - [idl] Support for union types and function (callback) types

#### [0.0.13] - 24'July, 2019
 - [idl] support for translating dictionaries, nullable types, array types and parameter types
 - [idl] support "implements" statement
 - redundant Unit return type in not printed anymore

#### [0.0.12] - 19'July, 2019
 - package name (if found) is added as a prefix to the name of the translated entity
 - [#73 - Union type unrolling is broken when it's aliased in a referenced file](https://github.com/Kotlin/dukat/issues/73)
 - [idl] Support typedefs in inheritance declarations 

#### [0.0.11] - 17'July, 2019
 - Experimental (and definitely incomplete) support for idl conversion
 - Add NESTED_CLASS_IN_EXTERNAL_INTERFACE only when it's needed
 - Merge modules into top level declarations with the same name whenever it's possible

#### [0.0.10] - 19'June, 2019
 - `<ROOT>` prefix should not end up in heritage clauses ([#56](https://github.com/Kotlin/dukat/issues/56))
 - Type aliases are moved out from files with file-level `JsQualifier` and `JsModule` annotations ([#41](https://github.com/Kotlin/dukat/issues/41))
 - Only files with `*.d.ts` extension are processed
 - TypeScript dependency updated to 3.5.2  
 - Graal-SDK updated to 19.0.0

#### [0.0.9] - 14'June, 2019
 - [#55 - Don't rename exported variables in external modules](https://github.com/Kotlin/dukat/issues/55)
 - [#31 - Remove redundant "definedExternally"](https://github.com/Kotlin/dukat/issues/31) 

#### [0.0.8] - 12'June, 2019
 - Fixing behaviour for resolving JsModules and JsQualifier (see [#45 - Export with assignment should produce JSModule annotation](https://github.com/Kotlin/dukat/issues/45))
 - [cli] -m option added so one can resolve JsModule names manually for files that are not in node_modules 
 - [dev] Make it possible to save optionally minimal report on dukat run ([#35 - Generate JSON report based on run results](https://github.com/Kotlin/dukat/issues/35)) 

#### [0.0.7] - 05'June, 2019
 - Resolving overrides parent entity is defined in stdlib ([#33 - Overrides not resolved for entities extending external objects](https://github.com/Kotlin/dukat/issues/33))
 - Fix for #9 (["Overrides are not resolved for parent entities defined in different files"](https://github.com/Kotlin/dukat/issues/9))
 - Allow custom qualified package name set from CLI ([#30 - Custom qualified package name will lead to a failure](https://github.com/Kotlin/dukat/issues/30)) 
 - Respect covariance in return types while resolving overrides
 - [#34](https://github.com/Kotlin/dukat/issues/34) - Processing lib references (like `/// <reference lib="es5" />`) - support for this is experimental and was introduced
 mainly for debugging purposes 

#### [0.0.6] - 31'May, 2019
 - All files by default are translated into the root package (so, no package declaration at all) - [#28](https://github.com/Kotlin/dukat/issues/28)
 - Translate file and all files referenced in it single run ([#22](https://github.com/Kotlin/dukat/issues/22))
 - [Omit computed property names](https://github.com/Kotlin/dukat/issues/27) - this is the best we can do right now
 - command line - In some cases files were not processed correctly from command line ([#26](https://github.com/Kotlin/dukat/issues/26) and [#])    
 - J2V8 js-backend removed 
 
#### [0.0.5] - 28'May, 2019
 - precise meta information for string literals
 - collapse unrolled callable entities if they differ only by metainformation
 - support ObjectTypeDeclaration as a return type in FunctionTypeDeclaration ([unable to process ParameterValueDeclaration ObjectLiteralDeclaration](https://github.com/Kotlin/dukat/issues/18))
 - recursive processing for polymorphic this ([Unable to process ParameterValueDeclaration ThisTypeDeclaration](https://github.com/Kotlin/dukat/issues/20))
 - process correctly UnionTypeDeclaration ([#17](https://github.com/Kotlin/dukat/issues/17)), IndexSignatureDeclaration ([#16](https://github.com/Kotlin/dukat/issues/16)), ConstructorDeclaration ([#15](https://github.com/Kotlin/dukat/issues/15)), IntersectionDeclaration ([#14](https://github.com/Kotlin/dukat/issues/14)) and FunctionTypeDeclaration ([#19](https://github.com/Kotlin/dukat/issues/19))

#### [0.0.4] - 24'May, 2019
 - Fix for override resolution in properties ([#5 -'property' overrides nothing](https://github.com/Kotlin/dukat/issues/5)) 
 - Support for Object Literals in generic constraints ([#4 - Dynamic type can not be used as an upper bound](https://github.com/Kotlin/dukat/issues/4))
 - [#8 - CLI Option -b some_pacakge_name gave no effect](https://github.com/Kotlin/dukat/issues/8)

#### [0.0.3] - 17'May, 2019
 - JVM target downgraded down to 1.6 ([Can't run on JRE 1.8](https://github.com/Kotlin/dukat/issues/1))
 - CLI works properly in windows
 - multiple fixes for declartions in heritage classes
 - resolve to "any" in case we've encountered unknown kind of declaration
 - nashorn js backend removed completely
 - support for string-literals in property names

#### [0.0.2] - 06'May, 2019  
 
 - support type aliasing for basic types
 - support for Partial wrapping object literal

#### [0.0.1] - 30'Apr, 2019
 
 - dukat npm package introduced 
