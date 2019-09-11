
var fs = require("fs");

function main(args) {
    var data = {NODE: args[0]};
    fs.writeFileSync(args[2], JSON.stringify(data, null, 2));
}

main(process.argv);