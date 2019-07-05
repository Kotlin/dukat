// based on browserify.d.ts
interface BrowserifyObject extends NodeJS.EventEmitter {
	add(file: string): BrowserifyObject;
	require(file: string): BrowserifyObject;
}

interface Options {
    entries?: string[];
    noParse?: string[];
}

declare module "browserify" {
	function browserify(): BrowserifyObject;
	function browserify(files: string[]): BrowserifyObject;
	function browserify(opts: Options): BrowserifyObject;

	export = browserify;
}
