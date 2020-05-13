[![official JetBrains project](https://jb.gg/badges/official.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
# Description
Converter of TypeScript definition files to Kotlin declarations

This requires JRE 1.6+ to run. It generates Kotlin files that are compatible with Kotlin 1.1+ (generated declarations
are tested against latest stable compiler version)

# How to install

The simplest way to use is install the latest version form [npm](https://www.npmjs.com/package/dukat):
```shell
npm install -g dukat
```

On a weekly basis we also deply a dev build which sums up what we currently have in master, 
so **if you want to checkout the snapshot version, use `dukat@next`**:

```
npm install -g dukat@next
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

### [0.0.28] - 13'February 2020
  - [descriptors] support for `inline` and `crosslinine` modifiers in descriptors
  - [typescript] Inlined invoke extension function can have return type
  - [typescript] Merge vars and interfaces even if they are in different files (but in the same package)
  - [typescript] Merge classlikes correctly (under some conditions they were copied after merge)
  - [typescript] Preserve type params while resolving this return type in extension functions    

### [0.0.27] - 07'February 2020
 - [build] make it possible to build with arbitrary version of kotlin compiler
 - [build] typescript compiler version updated to 3.5.3
 - [descriptors] support for compiling with 1.3.70-eap-42
 - [typescript] Move top level declarations into a separate file whenever it's invalid to keep them with the rest of declarations (that is, when there's file-level JsQualifier or JsModule annotations)
 - [idl] Don't add import for the same package this file belongs to
        
[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 
