// based on https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/jws/index.d.ts

import * as stream from 'stream';

//TODO: I'm not sure what we are testing here, this needs to be revisited
export interface SignStream extends stream.Readable {
    payload: stream.Writable;
    secret: any;
    key: any;
    privateKey: any;
    encoding?: string | stream.Readable;
}