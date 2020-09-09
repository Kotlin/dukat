import * as fs from "_fs";
import * as api from "_api";

import Stats = fs.Stats;

declare function calcBasedOnStats(s: Stats);
declare function calcStats(): Stats;

import IA = api.InterfaceA;
declare function ping(a: IA);