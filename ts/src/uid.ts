

//TODO: thoroughly revisit and decide whether we need something stricter
export function uid(): string {
  return (`1111-1111-1111-1111`).replace(/[1]/g, (v) => {
    let a = parseInt(v, 16);
    return (a ^ Math.random() * 16 >> a / 4).toString(16);
  });
}