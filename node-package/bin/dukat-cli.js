#!/usr/bin/env node

var exec = require('child_process').exec;
var path = require('path');


var processError = function(error) {
    if (error.code == 127) {
        console.log(
            "ERROR: It looks like you don't have java installed or it's just not reachable from command-line. \r\n"+
            "ERROR: As of now \"dukat\" requires Java Runtime Environment.");
    } else {
        console.error("ERROR: " + error.message);
    }
};

var execHandler = function(error, stdout, stderr) {
    if (error) {
        processError(error);
        process.exit(error.code);
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
    var classPath = [jsPath, cliPath].join(":");

    run("java -cp " + classPath + " org.jetbrains.dukat.cli.CliKt " + args.join(" "))
};

main();