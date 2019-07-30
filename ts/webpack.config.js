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
      "declarations": path.resolve("../typescript/tsmodel-proto/build/generated/source/proto/main/js/Declarations_pb.js")
    }
  },
  output: {
    path: path.resolve("build/bundle"),
    filename: "[name].js"
  }
};
