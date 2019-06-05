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

#### [0.0.7] - 05'June, 2019
 - Resolving overrides parent entity is defined in stdlib ([#33 - Overrides not resolved for entities extending external objects](https://github.com/Kotlin/dukat/issues/33))
 - Fix for #9 (["Overrides are not resolved for parent entities defined in different files"](https://github.com/Kotlin/dukat/issues/9))
 - Allow custom qualified package name set from CLI ([#30 - Custom qualified package name will lead to a failure](https://github.com/Kotlin/dukat/issues/30)) 
 - Respect covariance in return types while resolving overrides
 - [#34](https://github.com/Kotlin/dukat/issues/34) - Processing lib references (like `/// <reference lib="es5" />`) - support for this is experimental and was introduced
 mainly for debugging purposes 

#### [0.0.6] - 31'May, 2019
 - All files by default are translated into the root package (so, no package declaration at all) - [#28](https://github.com/Kotlin/dukat/issues/28)
 - Translate file and all files referenced in it single run ([#22](https://github.com/Kotlin/dukat/issues/22))
 - [Omit computed property names](https://github.com/Kotlin/dukat/issues/27) - this is the best we can do right now
 - command line - In some cases files were not processed correctly from command line ([#26](https://github.com/Kotlin/dukat/issues/26) and [#])    
 - J2V8 js-backend removed 
  
[see full CHANGELOG](https://github.com/Kotlin/dukat/blob/master/CHANGELOG.md)

# Useful links

- [TypeScript type definitions](https://github.com/DefinitelyTyped/DefinitelyTyped)
 