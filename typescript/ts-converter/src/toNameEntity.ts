import {NameEntity} from "./ast/ast";
import {AstFactory} from "./ast/AstFactory";

const astFactory = new AstFactory();

export function toNameEntity(name: string): NameEntity {
  if (name == "<ROOT>") {
    return astFactory.createIdentifierDeclarationAsNameEntity(name)
  } else {
    return name.split(".")
      .map(it => astFactory.createIdentifierDeclarationAsNameEntity(it))
      .reduce((acc, identifier) => {
         return concatenate(identifier, acc);
      })
  }
}

function concatenate(a: NameEntity, b: NameEntity) {
  if (b.hasIdentifier()) {
    return astFactory.createQualifiedNameDeclaration(a, b)
  } else if (b.hasQualifier()) {
    return concatenate(concatenate(a, b.getQualifier().getLeft()), b.getQualifier().getRight());
  }
}
