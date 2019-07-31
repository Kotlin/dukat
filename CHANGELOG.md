# Changelog

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
