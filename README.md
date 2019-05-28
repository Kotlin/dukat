# Description
Converter of TypeScript definition files to Kotlin declarations

This requires JRE 1.6+ to run. It generates Kotlin files that are compatible with Kotlin 1.3.30+.

# How to install

The simplest way to use is install the latest version form [npm](https://www.npmjs.com/package/dukat):
```shell
npm install -g dukat
```

# Usage

```shell
dukat [<options>] <d.ts files>
```

where possible options include:
```shell
    -p  <qualifiedPackageName>      package name for the generated file (by default filename.d.ts renamed to filename.d.kt)
    -d  <path>                      destination directory for files with converted declarations (by default declarations are generated in current directory)
    -js graal | j2v8                js-interop JVM engine (graal by default)
```

# How to setup and build

1. clone this project
  ```shell
  git clone <this project url>
  ```
  
2. build
 
 ```shell
 ./gradlew build
 ```
 
3. (optional) Run unit tests

```shell
./gradlew test -Pdukat.test.jsbackend.graal -Pdukat.test.failure.always
```  

# Recent Changes

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

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 