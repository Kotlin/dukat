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

#### [0.0.19] - 30'September, 2019
 - [Namespaced aliases lost in translation](https://github.com/Kotlin/dukat/issues/119)
 - Correct escaping for entities in heritage clauses, constraint params
 - Any toString method with no parameters is override
 - [Generic params information is lost for lambdas in types](https://github.com/Kotlin/dukat/issues/118)
   Support for generic params with default values pointing to some other generic params

#### [0.0.18] - 20'September, 2019
Starting from this release generated files names will
match following pattern:

 **[file_name].[packageName]?.[module_name]?.[additional_postfix]**

For instance, let's say we have file `foo.d.ts` in `fooController` package,
and following file is generated: `foo.helpers.module_fooController.kt`.
Then we know that the original file was called foo.d.ts, the file itself
belongs to the package "helpers" and npm module is fooController.

 - Module name resolving is based only on existence of package.json and its content (DUKAT-1 and DUKAT-4)
 - [Losing part of generated files because of the name clash](https://github.com/Kotlin/dukat/issues/109)
 - [Generated entities name clash, case-sensitivity related](https://github.com/Kotlin/dukat/issues/114)
 - [regression] -r (build report) command-line flag behaviour fixed
 - Batch mode removed completely - it was used for  dev purposes only and in it's current form proved to be quite inefficient


[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 