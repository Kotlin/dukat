// based on https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/types/browserify/index.d.ts

//TODO: currently both ts2k and dukat not supporting this, this causing a compilation error
import insertGlobals = require('insert-module-globals');

export = somethingfy;

declare namespace somethingfy {

    interface CustomOptions {
        /**
         * Custom properties can be defined on Options.
         * These options are forwarded along to module-deps and browser-pack directly.
         */
        [propName: string]: any;
        /** the directory that Browserify starts bundling from for filenames that start with .. */
        basedir?: string;
    }
    /**
     * Options pertaining to a Browserify instance.
     */
    interface Options extends CustomOptions {
        // will be passed to insert-module-globals as the opts.vars parameter.
        insertGlobalVars?: Any;
    }
}
