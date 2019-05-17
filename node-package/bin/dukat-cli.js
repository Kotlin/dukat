#!/usr/bin/env node

var exec = require('child_process').exec;
var path = require('path');

function isWin() {
    return process.platform == "win32";
}

var printError = function(errorMessage) {
    console.error("ERROR: " + errorMessage);
};

var processError = function(error) {
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

var execHandler = function(error, stdout, stderr) {
    if (error) {
        processError(error);
        process.exitCode = error.code;
    } else {
        console.log(stdout);
    }
};

var run = function(command) {
    exec(command, execHandler);
};

function printNoJava() {
    printError("It looks like you don't have java installed or it's just not reachable from command-line");
    printError("As of now \"dukat\" requires Java Runtime Environment to be installed.");
}

function guardJavaExists() {
    exec("java -version", function(error, stdout, stderr) {
        if (error) {
            printNoJava();
            process.exit(error.code);
        }
    });
}

var main = function() {
    var args = process.argv.slice(2);

    var runtimePath = path.resolve(__dirname + "/../build/runtime");
    var jsPath = path.resolve(runtimePath, "js.jar");
    var cliPath = path.resolve(runtimePath, "dukat-cli.jar");
    var classPath = [jsPath, cliPath].join(path.delimiter);

    guardJavaExists();
    run("java -cp " + classPath + " org.jetbrains.dukat.cli.CliKt " + args.join(" "))
};

main();