declare class GrandParent {
  constructor(description: String)
}

declare class Parent extends GrandParent {}

declare class Child extends Parent {}
declare class SelfishChild extends Parent {
  constructor(id: number)
}
