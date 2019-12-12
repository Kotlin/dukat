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

### [0.0.24] - 05'December, 2019
 - [In some cases](https://github.com/Kotlin/dukat/commit/041883a44448d6a591b1a922a9fb5a313cf16f2b) @Suppress("NESTED_CLASS_IN_EXTERNAL_INTERFACE") was missing.
 - Mutliple escaping issues fixes.
 - Rename params instead of escaping them (for instance, `object` renamed to just `obj`).
 - [Convert](https://github.com/Kotlin/dukat/commit/830041d502c633e0f70fa42e9e0cbe595bfb2adb) UnionType to string whenever it's possible. 
 - Resolve "import as" clauses and introduce imports accordingly.
 - [Resolve](https://github.com/Kotlin/dukat/commit/356d31872119a13426f4e38321041fec9803eb44) overrides for nested classes and interfaces.
 - Remove conflicting overloads.
 - [Treat](https://github.com/Kotlin/dukat/commit/7457cd692b67de7310285e6cd92f6265f8961001) names starting with dot as non-supportable. Unforunately, we need to introduce some changes in Kotlin/JS compiler itself for supporting property names not accessible via dot.
 - Always add JsNonModule alongside with JsModule.
 - Convert boolean literals to Boolean while converting types.
 - Copy methods generated from unrolled union types to ancestor classes.  

### [0.0.23] - 07'November, 2019
 - equals should have "override" modifier only when param is Any?
 - Resolve file names from namespaced nodejs packages.
 - Correct override resolving for methods with return type.
 - Resolve overrides when return type is generic.
 - Replace ReadonlyArray from ts stdlib with just Array.
 - Preserve TypeParams in unaliased entities in cases when they were lost.
 - Replace entity inherited from a final class (in a Kotlin stdlib sense with alias.

[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 