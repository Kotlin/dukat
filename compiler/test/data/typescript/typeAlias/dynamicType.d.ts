declare namespace Search {
  type MetadataSettings = Settings.Param;

  namespace Settings {
    type Param = (Pick<_Impl, 'cache'> |
      Pick<_Impl, 'results'>) &
      Partial<Pick<_Impl, keyof _Impl>>;

    class _Impl {
      cache: string;
      results: string;
    }
  }

  function ping(settings: MetadataSettings);
}
