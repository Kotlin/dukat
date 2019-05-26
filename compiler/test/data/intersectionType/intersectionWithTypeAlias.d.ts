export interface Coordinates {
    longitude: number;
    latitude: number;
}
export type CoordinatesScales = Coordinates & { zoom: number } | Coordinates & { scale: number };
declare function bestCoordinates(a: CoordinatesScales, b: CoordinatesScales): CoordinatesScales;

export class WithTrickyConstuctor {
    constructor(scales: CoordinatesScales)
}