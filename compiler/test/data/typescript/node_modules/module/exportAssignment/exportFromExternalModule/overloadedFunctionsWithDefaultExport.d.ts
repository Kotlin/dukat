interface Result  {}
interface Options {}

declare module "fluff" {
	function fluffify(): Result;
	function fluffify(files: string[]): Result;
	function fluffify(opts: Options): Result;

	export default fluffify;
}
