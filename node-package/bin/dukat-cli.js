#!/usr/bin/env node

var exec = require('child_process').exec;
var spawn = require('child_process').spawn;
var path = require('path');
require("../lib/converter");
var Readable = require('stream').Readable;

function isWin() {
    return process.platform == "win32";
}

var printError = function (errorMessage) {
    console.error("ERROR: " + errorMessage);
};

var processError = function (error) {
    printError("code: " + error.code);
    printError(error.message);
    if (!isWin()) {
        if (error.code == 1) {
            printError("File not found");
        } else if (error.code == 127) {
            // actually never supposed to be here because we have guardJavaExists()
            printNoJava()
        }
    }
};

var execHandler = function (error, stdout, stderr) {
    if (error) {
        processError(error);
        process.exitCode = error.code;
    } else {
        console.log(stdout);
    }
};

var run = function (command, args) {
    var child = spawn(command, args);
    process.stdin.pipe(child.stdin);

    child.stdout.pipe(process.stdout);
    child.stderr.pipe(process.stderr);

    child.on('exit', function () {
        process.exit()
    });

    return child;
};

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

function getVersion() {
    return require(path.resolve(__dirname, "..", "package.json")).version;
}

function createReadable() {
    var readable = new Readable();
    readable._read = function(chunk) {};

    readable.on("unpipe", function(){});
    return readable
}

function processArgs(args) {
    var skip_2args = new Set(["-d", "-p", "-m", "r"]);
    var count = 0;

    var packageName = "<ROOT>";

    while (count < args.length) {
        var arg = args[count];
        if (arg == "-p") {
            packageName = args[count + 1]
            count += 2;
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

    return {
        packageName: packageName,
        files: files
    }
}

function endsWith(str, postfix) {
    return str.lastIndexOf(postfix) == (str.length - postfix.length);
}


var main = function () {
    var args = process.argv.slice(2);

    var packageDir = path.resolve(__dirname, "..");
    var version = require(path.resolve(packageDir, "package.json")).version;

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
        var bundle = createBundle(path.resolve(packageDir, "d.ts.libs/lib.d.ts"), argsProcessed.packageName, files);

        var inputStream = createReadable();
        inputStream.push(bundle.serializeBinary());
        inputStream.push(null);

        var commandArgs = [
            "-Ddukat.cli.internal.packagedir=" + packageDir,
            "-Ddukat.cli.internal.version=" + getVersion(),
            "-cp", classPath, "org.jetbrains.dukat.cli.CliKt"].concat(args);

        var dukatProcess = run("java", commandArgs, {stdio: [process.stdin, process.stdout, process.stderr]});
        inputStream.pipe(dukatProcess.stdin);
    } else if (is_idl) {
        var commandArgs = [
            "-Ddukat.cli.internal.packagedir=" + packageDir,
            "-Ddukat.cli.internal.version=" + getVersion(),
            "-cp", classPath, "org.jetbrains.dukat.cli.CliKt"].concat(args);

        var dukatProcess = run("java", commandArgs, {stdio: [process.stdin, process.stdout, process.stderr]});
    }

};

main();