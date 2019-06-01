# Description
Converter of TypeScript definition files to Kotlin declarations

This requires JRE 1.6+ to run. It generates Kotlin files that are compatible with Kotlin 1.1+ (generated declarations
are tested against latest stable compiler version)

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
    -v, -version                    print version
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
./gradlew test -Pdukat.test.failure.always
```  

# Recent Changes

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
  
[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 