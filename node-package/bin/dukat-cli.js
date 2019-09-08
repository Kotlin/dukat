#!/usr/bin/env node

var exec = require('child_process').exec;
var spawn = require('child_process').spawn;
var path = require('path');
require("../lib/converter");
var Readable = require('stream').Readable;
var fs = require("fs");
var readline = require("readline");

function isWin() {
    return process.platform == "win32";
}

var printError = function (errorMessage) {
    console.error("ERROR: " + errorMessage);
};

var run = function (command, args) {
    var child = spawn(command, args);
    process.stdin.pipe(child.stdin);

    child.stdout.pipe(process.stdout);
    child.stderr.pipe(process.stderr);

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

function createReadable() {
    var readable = new Readable();
    readable._read = function(chunk) {};

    return readable
}

function processArgs(args) {
    var skip_2args = new Set(["-d", "-p", "-m", "r"]);
    var count = 0;

    var packageName = "<ROOT>";

    while (count < args.length) {
        var arg = args[count];
        if (arg == "-p") {
            packageName = args[count + 1];
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
        var bundle = createBundle(path.resolve(packageDir, "d.ts.libs/lib.d.ts"), argsProcessed.packageName, files);

        var inputStream = createReadable();
        inputStream.push(bundle.serializeBinary());
        inputStream.push(null);

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

        run("java", commandArgs);
    }
}

function eachLine(fileName, handler) {
    var lineReader = readline.createInterface({
        input: fs.createReadStream(fileName),
        crlfDelay: Infinity
    });

    var count = 0;
    lineReader.on("line", function (line) {
        if (!/^\s*$/.test(line)) {
            handler(line, count++);
        }
    });
}


var QUEUE = [];
var CURRENTLY_RUNNING = 0;
var POOL_SIZE = 32;

function batchMode(batchFile) {
    eachLine(batchFile, function(line, count) {
        var args = line.split(" ");
        console.log("[", count + 1, "] dukat", line);

        QUEUE.push(args);
        if (count === 0) {
            queueMode();
        }
    });
}

function queueMode() {
    if (QUEUE.length > 0) {
        var args = QUEUE.pop();
        if (Array.isArray(args)) {
            console.log("PROCESSING ", args);
            var proc = cliMode(args);
            if (proc) {
                CURRENTLY_RUNNING++;
                proc.on("exit", function(exitCode) {
                    proc.removeAllListeners();
                    CURRENTLY_RUNNING--;
                    console.log("EXITING ", proc.pid, exitCode);

                    for (var i = 0; i <= (POOL_SIZE - CURRENTLY_RUNNING); i++) {
                        queueMode();
                    }
                });
            }
        }
    }
}

var main = function (args) {
    if (Array.isArray(args) && (args[0] === "--batch")) {
        batchMode(args[1]);
    } else {
        cliMode(args);
    }
};

main(process.argv.slice(2));