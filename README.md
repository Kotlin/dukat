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

### [0.0.26] - 24'January 2020
 - [In some cases](https://github.com/Kotlin/dukat/commit/76050f8fd260a470e152fd508fec5bf7523f4bb0) something that is a valid override in typescript is not an override in kotlin, we have to copy overriden method to the descendant class.
 - [Rename](https://github.com/Kotlin/dukat/commit/f03826e7cb5a86a1e634d9cdb481df56daeeac78) class if it's name clashes with the named import
 - [Types](https://github.com/Kotlin/dukat/commit/8d1f0515aa998a6c721253a1c1cfc15ffeac5f3d) from optional params are not forcibly converted to nullables
    Inline extension functions generated from interfaces are unrolled if there're optional params.

### [0.0.25] - 15'January, 2020
 - Minimal CLI support for compiling binary descriptors (this one so far is for dev purposed)
 - Some overrides in Typescript are not treated as such in Kotlin, so we have to add reintroduce them to the descendant classes
 - Filter out all class parents except the very first one for classes    

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

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 