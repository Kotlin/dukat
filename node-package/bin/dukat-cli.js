#!/usr/bin/env node

var exec = require('child_process').exec;
var path = require('path');

var execHandler = function(error, stdout, stderr) {
    if (error) {
        console.error(stderr)
    } else {
        console.log(stdout)
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