var http = require("http");
var path = require("path");

const PROJECT_DIR = path.resolve(__dirname, "../../../../../../../..");
var dukatCli = require("../../../../../../../../node-package/build/distrib/bin/dukat-cli.js");

function createServer(port) {
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
                    let relpath = path.relative(PROJECT_DIR, file);
                    if (relpath.startsWith("..")) {
                        console.log(`skipping ${file} since it does not belong to the project dir (${PROJECT_DIR})`);
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
    createServer(process.argv[2] || 8090);
}

main();
