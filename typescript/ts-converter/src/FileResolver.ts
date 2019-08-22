import * as fs from "fs";
import {createLogger} from "./Logger";

export class FileResolver {
  private static logger = createLogger("fileResolver");

  private static readFile(fileName: string): string {
    return fs.readFileSync(fileName, "utf-8");
  }

  resolve(fileName: string): string {
    FileResolver.logger.debug(`resolving ${fileName}`);
    return FileResolver.readFile(fileName);
  }
}