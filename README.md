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

### [0.0.27] - 07'February 2020
 - [build] make it possible to build with arbitrary version of kotlin compiler
 - [build] typescript compiler version updated to 3.5.3
 - [descriptors] support for compiling with 1.3.70-eap-42
 - [typescript] Move top level declarations into a separate file whenever it's invalid to keep them with the rest of declarations (that is, when there's file-level JsQualifier or JsModule annotations)
 - [idl] Don't add import for the same package this file belongs to

### [0.0.26] - 24'January 2020
 - [In some cases](https://github.com/Kotlin/dukat/commit/76050f8fd260a470e152fd508fec5bf7523f4bb0) something that is a valid override in typescript is not an override in kotlin, we have to copy overriden method to the descendant class.
 - [Rename](https://github.com/Kotlin/dukat/commit/f03826e7cb5a86a1e634d9cdb481df56daeeac78) class if it's name clashes with the named import
 - [Types](https://github.com/Kotlin/dukat/commit/8d1f0515aa998a6c721253a1c1cfc15ffeac5f3d) from optional params are not forcibly converted to nullables
    Inline extension functions generated from interfaces are unrolled if there're optional params.

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 