import {translate} from "./converter";
import * as fs from "fs";

function runtime() {
  if (process.argv.length > 2) {
    let args = process.argv.slice(2);
    let libName = args.shift();
    if (libName == "--no-lib") {
      libName = ""
    }

    let outputName = args.shift();

    let sourceSetBundle = translate(libName || "", args);

    let writeStream = (outputName == "--std-out") ? process.stdout : fs.createWriteStream(outputName!!);
    writeStream.write(sourceSetBundle.serializeBinary() as any)
  }
}

runtime();