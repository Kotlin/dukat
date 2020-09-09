declare class Album {
  label: Album.AlbumLabel;
}

declare namespace Album {
  type Playlist = (id: string, data: any | null) => void;
  function play(album: Album, playlist: Playlist | null);
  export class AlbumLabel {
    static defaultLabel: AlbumLabel;
    songsCount(): number;
  }
}