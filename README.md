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

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 