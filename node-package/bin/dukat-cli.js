#!/usr/bin/env node

var exec = require('child_process').exec;
var path = require('path');

var printError = function(errorMessage) {
    console.error("ERROR: " + errorMessage);
};

var processError = function(error) {
    printError(error.message);
    if ((error.code == 127) || (error.code == 1)) {
        printError("It looks like you don't have java installed or it's just not reachable from command-line");
        printError("As of now \"dukat\" requires Java Runtime Environment to be installed.");
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

var main = function() {
    var args = process.argv.slice(2);

    var runtimePath = path.resolve(__dirname + "/../build/runtime");
    var jsPath = path.resolve(runtimePath, "js.jar");
    var cliPath = path.resolve(runtimePath, "dukat-cli.jar");
    var classPath = [jsPath, cliPath].join(path.delimiter);

    run("java -cp " + classPath + " org.jetbrains.dukat.cli.CliKt " + args.join(" "))
};

main();