#!/usr/bin/env node

var exec = require('child_process').exec;
var spawn = require('child_process').spawn;
var path = require('path');
var fs = require('fs');

var createSourceSet = require("../lib/converter").createSourceSet;
var Readable = require('stream').Readable;
var EventEmitter = require('events');

var printError = function (errorMessage) {
    console.error("ERROR: " + errorMessage);
};

function run(command, args) {
    var child = spawn(command, args);
    process.stdin.pipe(child.stdin);

    child.stdout.pipe(process.stdout);
    child.stderr.pipe(process.stderr);

    return child;
}

function printNoJava() {
    printError("It looks like you don't have java installed or it's just not reachable from command-line");
    printError("As of now \"dukat\" requires Java Runtime Environment to be installed.");
}

function guardJavaExists() {
    exec("java -version", function (error, stdout, stderr) {
        if (error) {
            printNoJava();
            process.exit(error.code);
        }
    });
}

function createReadable() {
    var readable = new Readable();
    readable._read = function(chunk) {};

    return readable
}

function getStdLib() {
    return path.resolve(__dirname, "..", "d.ts.libs/lib.es6.d.ts");
}

function processArgs(args) {
    var skip_2args = new Set(["-d", "-p", "-m", "-r"]);
    var ordinary_args = new Set(["--descriptors"]);
    var count = 0;

    var binaryOutput = null;
    var stdlib = getStdLib();
    var tsConfig = null;

    while (count < args.length) {
        var arg = args[count];
        if (arg == "-b") {
            binaryOutput = args[count + 1];
            count += 2;
        } else if (arg == "--ts-config") {
            tsConfig = args[count + 1];
            count += 2;
        } else if(arg == "-l") {
            stdlib = args[count + 1];
            count += 2;
        } else if (skip_2args.has(arg)) {
            count += 2;
        } else if (ordinary_args.has(arg)) {
            count++;
        } else {
            break;
        }
    }

    var files = [];
    if (count < args.length) {
        files = args.slice(count).map(function(arg) {
            return path.resolve(arg);
        });
    }

    return {
        stdlib: stdlib,
        binaryOutput: binaryOutput,
        files: files,
        tsConfig: tsConfig
    };
}

function endsWith(str, postfix) {
    return str.lastIndexOf(postfix) == (str.length - postfix.length);
}



function createBinary(tsConfig, stdlib, emitDiagnostics, files) {
    return createSourceSet(tsConfig, stdlib, emitDiagnostics, files);
}

function createReadableStream(binary, onData, onEnd) {
    var readable = createReadable();

    if (typeof onData == "function") {
        readable.on("data", onData);
    }

    if (typeof onEnd == "function") {
        readable.on("end", onEnd);
    }

    readable.push(binary.serializeBinary());
    readable.push(null);

    return readable;
}

function cliMode(args) {
    var packageDir = path.resolve(__dirname, "..");

    if (args[0] == "-v" || args[0] == "version") {
        var version = require(path.resolve(packageDir, "package.json")).version;
        console.log("dukat version " + version);
        return;
    }

    var runtimePath = path.resolve(packageDir, "build/runtime");
    var cliPath = path.resolve(runtimePath, "dukat-cli.jar");
    var classPath = [cliPath].join(path.delimiter);

    guardJavaExists();

    var argsProcessed = processArgs(args);

    var files = argsProcessed.files;
    var is_ts = files.every(function(file) { return endsWith(file, ".d.ts") || endsWith(file, ".ts")});
    var is_idl = files.every(function(file) { return endsWith(file, ".idl") || endsWith(file, ".webidl")});
    var is_js = files.every(function(file) { return endsWith(file, ".js")});

    if (is_ts || is_js) {
        try {
            let bundle = createBinary(argsProcessed.tsConfig, argsProcessed.stdlib, false, files);

            var inputStream = createReadableStream(bundle);

            if (typeof argsProcessed.binaryOutput == "string") {
                inputStream.pipe(fs.createWriteStream(argsProcessed.binaryOutput));
                return null;
            }

            var commandArgs = [
                "-Ddukat.cli.internal.packagedir=" + packageDir,
                "-cp", classPath, "org.jetbrains.dukat.cli.CliKt"].concat(args);

            var dukatProcess = run("java", commandArgs);
            inputStream.pipe(dukatProcess.stdin);
            return dukatProcess;
        } catch (e) {
            if (e.hasOwnProperty("tsDiagnostic")) {
                console.log(`failed to parse tsconfig: ${argsProcessed.tsConfig}`);
            } else {
                console.log("unresolved exception");
            }
            console.log(e);
            process.exit(1);
        }

    } else if (is_idl) {
        var commandArgs = [
            "-Ddukat.cli.internal.packagedir=" + packageDir,
            "-cp", classPath, "org.jetbrains.dukat.cli.CliKt"].concat(args);

        return run("java", commandArgs);
    }

    process.exit(1);
}

var main = function (args) {
    var childProcess = cliMode(args);
    if (childProcess instanceof EventEmitter) {
        childProcess.on("exit", function() {
            process.exit();
        });
    }
};

if (require.main === module) {
    main(process.argv.slice(2));
}

exports.translate = main;
exports.createBinary = createBinary;
exports.createReadableStream = createReadableStream;
exports.getStdLib = getStdLib;