interface Dog {}
interface Cat {}
interface Sheep {}
interface Animal {}

interface Cloner {
  clone(animal: Animal): Animal;
}

interface Cloner {
  clone(animal: Sheep): Sheep;
}

interface CompletelyIrrelevantInterface {
  clone(animal: Dog): Dog;
  clone(animal: Cat): Cat;
}

interface Cloner {
  clone(animal: Dog): Dog;
  clone(animal: Cat): Cat;
}

