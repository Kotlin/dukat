var http = require("http");
var path = require("path");
var cluster = require('cluster');
var os = require('os');

var dukatCli = require("../../../../../../../../node-package/build/distrib/bin/dukat-cli.js");


function ok(res) {
    res.setHeader('Content-Type', 'text/plain');
    res.statusCode = 200;
    res.end("OK");
}

function createServer(port, sandboxDirs) {
    console.log(`starting server at port ${port} (pid = ${process.pid})`);

    var server = http.createServer(function (req, res) {
        if (req.method === 'GET') {
            if (req.url === '/status') {
                ok(res);
            } else if (req.url === '/shutdown') {
                ok(res);
                process.send("shutdown");
            }
        }
        if (req.method === 'POST' && req.url === '/dukat') {
            var body = [];
            req.on('data', (chunk) => {
                body.push(chunk);
            }).on('end', function () {
                var bodyRaw = Buffer.concat(body).toString();
                var data = JSON.parse(bodyRaw);

                var onBinaryStreamData = function (chunk) {
                    res.write(chunk);
                };
                var onBinaryStreamEnd = function (chunk) {
                    res.end();
                };

                let files = data.files.filter(file => {
                    let fileIsSandboxed = sandboxDirs.some(sandboxDir => !path.relative(sandboxDir, file).startsWith(".."));
                    if (!fileIsSandboxed) {
                        console.log(`skipping ${file} since it does not belong to any of sandbox locations: ${sandboxDirs}`);
                        return false;
                    }

                    return true;
                });

                try {
                    res.setHeader('Content-Type', 'application/octet-stream');
                    dukatCli.createReadableStream(
                        dukatCli.createBinary(
                            data.tsConfig,
                            dukatCli.getStdLib(),
                            false,
                            files
                        ),
                        onBinaryStreamData,
                        onBinaryStreamEnd
                    );
                } catch (e) {
                    res.setHeader('Content-Type', 'text/plain');
                    res.statusCode = 500;
                    console.log(e);
                    res.end(e.toString());
                }
            });
        }
    });

    server.listen(port, function () {
        console.log(`server (pid=${process.pid}) is up`);
    });

    server.on("error", function(err) {
        console.log(err);
        if (err.code == "EADDRINUSE") {
            console.log("address is already in use but most likely it's fine");
        }
        cluster.worker.send("shutdown")
    })
}

function shutdown(cluster) {
    let connectedWorkers = Object.values(cluster.workers).filter(it => it.connected).map(it => {
        try {
            if (it.connected) {
                it.kill();
            }
        } catch (e) {}

        return it
    }
    );
    console.log(`${connectedWorkers.length} connected workers found`);
    if (connectedWorkers.length > 0) {
        setTimeout(function () {
            shutdown(cluster)
        }, 200);
    } else {
        process.exit();
    }
}

function createCluster(port, sandboxDirs) {
    if (cluster.isMaster) {
        console.log(`sandbox ${sandboxDirs}`);

        cluster.on("message", function(worker, message) {
            if (message == "shutdown") {
                shutdown(cluster);
            }
        });

        var cpus = os.cpus();
        console.log(`CPU count => ${cpus.length}`);


        cpus.forEach(_ => {
            let worker = cluster.fork();

            worker.on('disconnect', function () {
                console.log(`disconnecting ${worker.process.pid}`);
            });
        });
    } else {
        createServer(port, sandboxDirs);
    }
}

function main() {
    const projectDir = path.resolve(__dirname, "../../../../../../../..");
    let sandboxDirs = process.argv.slice(3);
    sandboxDirs.push(projectDir);
    createCluster(process.argv[2], sandboxDirs.map(it => path.resolve(it)));
}

main();
