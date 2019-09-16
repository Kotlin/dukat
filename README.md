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

#### [0.0.17] - 16'September, 2019
 - This release contains mainly idl-related changes. The idl target is considered production-ready,
 however its support is still experimental. 

#### [0.0.16] - 16'August, 2019
#### [0.0.15] - 16'August, 2019
 - The only change is speed, however, this change is big. Since this release ts is parsed on nodejs side and
 passed in a binary form to the JVM side.

#### [0.0.14] - 8'August, 2019
 - Add global module annotation in case we have default export but don't have module annotation so far
 - [idl] Allow translation of several files (dependent on each other)
 - [idl] Introduce getters/setters and enums
 - [idl] Support for union types and function (callback) types

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 