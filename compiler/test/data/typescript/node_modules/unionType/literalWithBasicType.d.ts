type AlphaKey = 'sameDay' | 'nextDay' | 'lastDay' | 'nextWeek' | 'lastWeek' | 'sameElse' | string;
type NumKey = 1 | 2 | 3 | number;

interface Locale {
  ping(key?: AlphaKey): string;
  pong(key?: NumKey): string;
}


