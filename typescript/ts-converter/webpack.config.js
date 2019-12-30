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
      "declarations": path.resolve("../ts-model-proto/build/generated/source/proto/main/js/Declarations_pb")
    }
  },
  externals: {
    "google-protobuf": {
      commonjs: 'google-protobuf'
    },
    "typescript": {
      commonjs: 'typescript'
    }
  },
  output: {
    libraryTarget: "commonjs",
    path: path.resolve("build/bundle"),
    filename: "[name].js"
  }
};
