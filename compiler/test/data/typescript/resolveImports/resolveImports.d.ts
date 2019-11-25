declare const enum LevelEnum {
  None = 0,
  Basic = 1,
  Advanced = 2,
  Nightmare = 3
}

declare namespace api {
  type Level = LevelEnum;

  interface Options {
    level?: Level;
  }
}

