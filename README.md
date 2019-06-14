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

#### [0.0.9] - 14'June, 2019
 - [#55 - Don't rename exported variables in external modules](https://github.com/Kotlin/dukat/issues/55)
 - [#31 - Remove redundant "definedExternally"](https://github.com/Kotlin/dukat/issues/31)

#### [0.0.8] - 12'June, 2019
 - Fixing behaviour for resolving JsModules and JsQualifier (see [#45 - Export with assignment should produce JSModule annotation](https://github.com/Kotlin/dukat/issues/45))
 - [cli] -m option added so one can resolve JsModule names manually for files that are not in node_modules 
 - [dev] Make it possible to save optionally minimal report on dukat run ([#35 - Generate JSON report based on run results](https://github.com/Kotlin/dukat/issues/35))
  
[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 