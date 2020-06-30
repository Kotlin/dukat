var http = require("http");
var path = require("path");

var dukatCli = require("../../../../../../../../node-package/build/distrib/bin/dukat-cli.js");

function createServer(port, sandboxDirs) {
    console.log(`starting server at port ${port} with following sandboxed dirs: ${sandboxDirs}`);

    var server = http.createServer(function (req, res) {
        if (req.method === 'GET' && req.url === '/status') {
            res.setHeader('Content-Type', 'text/plain');
            res.statusCode = 200;
            res.end("OK");
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


    server.listen(port);
}

function main() {
    const projectDir = path.resolve(__dirname, "../../../../../../../..");
    let sandboxDirs = process.argv.slice(3);
    sandboxDirs.push(projectDir);
    createServer(process.argv[2], sandboxDirs.map(it => path.resolve(it)));
}

main();
