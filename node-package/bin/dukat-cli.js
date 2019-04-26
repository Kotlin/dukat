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
<<<<<<< HEAD
    run("java -cp build/dukat-cli.jar org.jetbrains.dukat.cli.CliKt")
=======
    run("java -cp ./build/runtime/js.jar:./build/runtime/dukat-cli.jar org.jetbrains.dukat.cli.CliKt")
>>>>>>> NPM_PACKAGE_WITH_CLASSPATH
};

main();