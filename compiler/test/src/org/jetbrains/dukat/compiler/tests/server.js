const http = require("http");
const dukatCli = require("../../../../../../../../node-package/build/distrib/bin/dukat-cli.js");

function createServer(port) {

    let server = http.createServer(function (req, res) {
        if (req.method === 'GET' && req.url === '/status') {
            res.setHeader('Content-Type', 'text/plain');
            res.statusCode = 200;
            res.end("OK");
        }
        if (req.method === 'POST' && req.url === '/dukat') {
            let body = [];
            req.on('data', (chunk) => {
                body.push(chunk);
            }).on('end', function () {
                let bodyRaw = Buffer.concat(body).toString();
                let data = JSON.parse(bodyRaw);

                let onBinaryStreamData = function (chunk) {
                    res.write(chunk);
                };
                let onBinaryStreamEnd = function (chunk) {
                    res.end();
                };

                try {
                    res.setHeader('Content-Type', 'application/octet-stream');
                    dukatCli.createBinaryStream(
                        data.packageName,
                        data.files,
                        onBinaryStreamData,
                        onBinaryStreamEnd,
                        data.useStdLib
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
