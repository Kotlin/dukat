# Description
Converter of TypeScript definition files to Kotlin declarations

This requires JRE 1.6+ to run. It generates Kotlin files that are compatible with Kotlin 1.3.30+.


# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)


# Changelog

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
