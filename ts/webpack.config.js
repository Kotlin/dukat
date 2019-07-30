var path = require("path");

module.exports = {
  mode: "none",
  entry: {
    "converter": [
      path.resolve("./build/ts/dukat-ast-builder.js")
    ]
  },
  resolve: {
    alias: {
      "declarations": path.resolve("../typescript/tsmodel-proto/build/generated/source/proto/main/js/Declarations_pb.js"),
      "google-protobuf": path.resolve("./build/package/node_modules/google-protobuf")
    }
  },
  output: {
    path: path.resolve("build/bundle"),
    filename: "[name].js"
  }
};
