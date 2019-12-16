var path = require("path");

module.exports = {
  mode: "none",
  target: "node",
  entry: {
    "converter": [
      path.resolve("./build/ts/converter.js")
    ],
    "runtime": [
      path.resolve("./build/ts/runtime.js")
    ]
  },
  resolve: {
    alias: {
      "declarations": path.resolve("../ts-model-proto/build/generated/source/proto/main/js/Declarations_pb"),
      "google-protobuf": path.resolve("./build/package/node_modules/google-protobuf"),
      "typescript": path.resolve("./build/package/node_modules/typescript/lib/typescript.js")
    }
  },
  output: {
    path: path.resolve("build/bundle"),
    filename: "[name].js"
  }
};
