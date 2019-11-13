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
    -m  String                      use this value as @file:JsModule annotation value whenever such annotation occurs
    -d  <path>                      destination directory for files with converted declarations (by default declarations are generated in current directory)
    -v, -version                    print version
```

# How to setup and build

1. clone this project
  ```shell
  # on Windows-based platforms set following: `git config core.autocrlf true`   
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

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 