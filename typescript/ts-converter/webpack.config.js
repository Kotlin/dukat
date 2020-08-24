var path = require("path");

module.exports = {
  mode: "none",
  target: "node",
  entry: {
    "converter": [
      path.resolve(__dirname, "build/ts/converter.js")
    ]
  },
  resolve: {
    alias: {
      "declarations": path.resolve(__dirname, "../ts-model-proto/build/generated/source/proto/main/js/tsdeclarations_pb"),
      "common_declarations": path.resolve(__dirname, "../ts-model-proto/build/generated/source/proto/main/js/common_pb")
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
    path: path.resolve(__dirname, "build/bundle"),
    filename: "[name].js"
  }
};
