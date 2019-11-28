declare interface Album {
  label: Album.AlbumLabel;
}

declare namespace Album {
  function play(album: Album);
  export class AlbumLabel {
    static defaultLabel: AlbumLabel;
    songsCount(): number;
  }
}