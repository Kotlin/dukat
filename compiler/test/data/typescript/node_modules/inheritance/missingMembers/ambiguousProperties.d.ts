interface InputOptions {
  highWaterMark?: number;
  encoding?: string;
  objectMode?: boolean;
}

interface OutputOptions {
  highWaterMark?: number;
  decodeStrings?: boolean;
  objectMode?: boolean;
  defaultEncoding?: string;
}

interface DuplexOptions extends InputOptions, OutputOptions {
}