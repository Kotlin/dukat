var path = require('path');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');


module.exports = {
  mode: "none",

  entry: {
    converter: [
      './build/ts/converter.js'
    ]
  },

  output: {
    path: path.resolve("build/bundle"),
    filename: "[name].bundle.js",
    library: ['MODULE']
  },

  target: "node",
}
