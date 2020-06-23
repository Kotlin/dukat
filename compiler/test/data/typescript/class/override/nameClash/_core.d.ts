declare namespace Core {
  interface SomeEvent {}

  interface SomeDomain {
    add(evt: SomeEvent);
  }
}