
declare module a {
  function ping(a: b.AlphaNumeric);
}

declare module b {
  type AlphaNumeric = string | number
}

export = a.ping