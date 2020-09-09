/// <reference path="./_api.d.ts" />

import {SyncHook} from "_api";

interface Module {}

declare class SomeAPI {
  buildModule(): SyncHook<Module>;
}