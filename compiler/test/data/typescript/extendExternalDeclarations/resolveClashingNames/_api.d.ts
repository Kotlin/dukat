/// <reference path="_oldapi.d.ts"/>
/// <reference path="_specificapi.d.ts"/>

interface API {
  ping(): boolean;
  pong(a: string): boolean
}