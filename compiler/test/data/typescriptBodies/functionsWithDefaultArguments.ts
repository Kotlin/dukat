interface Settings {}

function withOneAny(a: any = 0): any {
  return a;
}

function withOneString(s: string = "foobar"): string {
  return s;
}

function withOneStringAndOptional(s: string = "something", settings?: Settings): boolean {
  if (settings && s == "anything") {
    return true;
  }

  return false;
}
