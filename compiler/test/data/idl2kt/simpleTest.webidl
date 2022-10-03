package simplePackage;

interface SimpleBase {
  readonly attribute DOMString field;
  attribute SomeInterface? dynamicField;
};

interface Simple : SimpleBase {
getter SimpleBase? item(unsigned long index);
  readonly attribute unsigned long length;
  void delete(unsigned long index);
};