#!/usr/bin/env node

var exec = require('child_process').exec;

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
    run("java -cp build/dukat-cli.jar org.jetbrains.dukat.cli.CliKt")
};

main();