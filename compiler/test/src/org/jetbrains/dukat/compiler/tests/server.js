var http = require("http");
var path = require("path");
var cluster = require('cluster');
var os = require('os');

var dukatCli = require("../../../../../../../../node-package/build/distrib/bin/dukat-cli.js");

function closeServer(server, socketsSet) {
    console.log("shutting down server");
    server.close(function() {
        console.log("server is down");
    });
    console.log(`${socketsSet.size} connections to destroy`);
    socketsSet.forEach(socket => {
        socket.destroy();
    })
}

function ok(res) {
    res.setHeader('Content-Type', 'text/plain');
    res.statusCode = 200;
    res.end("OK");
}

function createServer(port, sandboxDirs) {
    console.log(`starting server at port ${port} with following sandboxed dirs: ${sandboxDirs}`);
    var socketsSet = new Set();

    var server = http.createServer(function (req, res) {
        if (req.method === 'GET') {
            if (req.url === '/status') {
                ok(res);
            } else if (req.url === '/shutdown') {
                ok(res);
                closeServer(server, socketsSet);
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

    server.on("connection", function(socket) {
        socketsSet.add(socket);

        socket.on("close", function() {
            socketsSet.delete(socket)
        });
    })


    server.listen(port, function() {
        console.log("server is up");
    });
}

function createCluster(port, sandboxDirs) {
    if (cluster.isMaster) {
        var cpus = os.cpus();
        console.log(`CPU count => ${cpus.length}`);

        cpus.forEach( _ => {
            cluster.fork();
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
