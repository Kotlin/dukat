import * as fs from "_fs";
import Stats = fs.Stats;

declare function calcBasedOnStats(s: Stats);
declare function calcStats(): Stats;