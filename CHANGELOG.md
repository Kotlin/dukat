# Changelog

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
