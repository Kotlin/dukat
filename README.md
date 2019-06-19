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

#### [0.0.10] - 19'June, 2019
 - `<ROOT>` prefix should not end up in heritage clauses ([#56](https://github.com/Kotlin/dukat/issues/56))
 - Type aliases are moved out from files with file-level `JsQualifier` and `JsModule` annotations ([#41](https://github.com/Kotlin/dukat/issues/41))
 - Only files with `*.d.ts` extension are processed
 - TypeScript dependency updated to 3.5.2  
 - Graal-SDK updated to 19.0.0

#### [0.0.9] - 14'June, 2019
 - [#55 - Don't rename exported variables in external modules](https://github.com/Kotlin/dukat/issues/55)
 - [#31 - Remove redundant "definedExternally"](https://github.com/Kotlin/dukat/issues/31)

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 