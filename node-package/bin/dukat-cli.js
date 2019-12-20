#!/usr/bin/env node

var exec = require('child_process').exec;
var spawn = require('child_process').spawn;
var path = require('path');

var createBundle = require("../lib/converter").createBundle;
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

function processArgs(args) {
    var skip_2args = new Set(["-d", "-p", "-m", "-r"]);
    var count = 0;

    var packageName = "<ROOT>";
    var binaryOutput = false;

    while (count < args.length) {
        var arg = args[count];
        if (arg == "-p") {
            packageName = args[count + 1];
            count += 2;
        } else if(arg == "-b") {
            binaryOutput = true;
            count += 1;
        } else if (skip_2args.has(arg)) {
            count += 2;
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

    var res = {
        binaryOutput: binaryOutput,
        packageName: packageName,
        files: files
    };

    return res;
}

function endsWith(str, postfix) {
    return str.lastIndexOf(postfix) == (str.length - postfix.length);
}


function createBinaryStream(packageName, files, onData, onEnd) {
    var DEFAULT_LIB_PATH = "d.ts.libs/lib.es6.d.ts";
    var stdlib = path.resolve(__dirname, "..", DEFAULT_LIB_PATH);

    var bundle = createBundle(stdlib, packageName, files);

    var readable = createReadable();

    if (typeof onData == "function") {
        readable.on("data", onData);
    }

    if (typeof onEnd == "function") {
        readable.on("end", onEnd);
    }

    readable.push(bundle.serializeBinary());
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
    var jsPath = path.resolve(runtimePath, "js.jar");
    var cliPath = path.resolve(runtimePath, "dukat-cli.jar");
    var classPath = [jsPath, cliPath].join(path.delimiter);

    guardJavaExists();

    var argsProcessed = processArgs(args);

    var files = argsProcessed.files;
    var is_ts = files.every(function(file) { return endsWith(file, ".d.ts")});
    var is_idl = files.every(function(file) { return endsWith(file, ".idl") || endsWith(file, ".webidl")});

    if (is_ts) {
        var inputStream = createBinaryStream(argsProcessed.packageName, files);

        if (argsProcessed.binaryOutput) {
            inputStream.pipe(process.stderr);
            return null;
        }

        var commandArgs = [
            "-Ddukat.cli.internal.packagedir=" + packageDir,
            "-cp", classPath, "org.jetbrains.dukat.cli.CliKt"].concat(args);

        var dukatProcess = run("java", commandArgs);
        inputStream.pipe(dukatProcess.stdin);
        return dukatProcess;
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
exports.createBinaryStream = createBinaryStream;
