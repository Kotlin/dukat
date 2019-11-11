import * as ts from "typescript-services-api";
import {
    Expression,
    FunctionDeclaration,
    IdentifierEntity,
    MemberDeclaration,
    NameEntity,
    PropertyDeclaration
} from "./ast";
import {AstExpressionFactory} from "./AstExpressionFactory";
import {AstConverter} from "../AstConverter";
import {ObjectLiteralElementLike} from "../../.tsdeclarations/typescript";

export class AstExpressionConverter {
    constructor(
        private astConverter: AstConverter
    ) {
    }

    createBinaryExpression(left: Expression, operator: string, right: Expression): Expression {
        return AstExpressionFactory.createBinaryExpressionDeclarationAsExpression(left, operator, right);
    }

    createUnaryExpression(operand: Expression, operator: string, isPrefix: boolean) {
        return AstExpressionFactory.createUnaryExpressionDeclarationAsExpression(operand, operator, isPrefix);
    }

    createTypeOfExpression(expression: Expression) {
        return AstExpressionFactory.createTypeOfExpressionDeclarationAsExpression(expression);
    }

    createCallExpression(expression: Expression, args: Array<Expression>) {
        return AstExpressionFactory.createCallExpressionDeclarationAsExpression(expression, args);
    }

    createPropertyAccessExpression(expression: Expression, name: IdentifierEntity) {
        return AstExpressionFactory.createPropertyAccessExpressionDeclarationAsExpression(expression, name);
    }

    createElementAccessExpression(expression: Expression, argumentExpression: Expression) {
        return AstExpressionFactory.createElementAccessExpressionDeclarationAsExpression(expression, argumentExpression);
    }

    createNameExpression(name: NameEntity): Expression {
        return AstExpressionFactory.createNameExpressionDeclarationAsExpression(name)
    }

    createNumericLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createNumericLiteralDeclarationAsExpression(value);
    }

    createBigIntLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createBigIntLiteralDeclarationAsExpression(value);
    }

    createStringLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createStringLiteralDeclarationAsExpression(value);
    }

    createBooleanLiteralExpression(value: boolean): Expression {
        return AstExpressionFactory.createBooleanLiteralDeclarationAsExpression(value);
    }

    createObjectLiteralExpression(members: Array<MemberDeclaration>): Expression {
        return AstExpressionFactory.createObjectLiteralDeclarationAsExpression(members);
    }

    createRegExLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createRegExLiteralDeclarationAsExpression(value);
    }

    createUnknownExpression(value: string): Expression {
        return AstExpressionFactory.createUnknownExpressionDeclarationAsExpression(value);
    }


    convertBinaryExpression(expression: ts.BinaryExpression): Expression {
        return this.createBinaryExpression(
            this.convertExpression(expression.left),
            ts.tokenToString(expression.operatorToken.kind),
            this.convertExpression(expression.right)
        )
    }

    convertPrefixUnaryExpression(expression: ts.PrefixUnaryExpression): Expression {
        return this.createUnaryExpression(
            this.convertExpression(expression.operand),
            ts.tokenToString(expression.operator),
            true
        )
    }

    convertPostfixUnaryExpression(expression: ts.PostfixUnaryExpression): Expression {
        return this.createUnaryExpression(
            this.convertExpression(expression.operand),
            ts.tokenToString(expression.operator),
            false
        )
    }

    convertTypeOfExpression(expression: ts.TypeOfExpression): Expression {
        return this.createTypeOfExpression(
            this.convertExpression(expression.expression),
        )
    }

    convertCallExpression(expression: ts.CallExpression): Expression {
        return this.createCallExpression(
            this.convertExpression(expression.expression),
            expression.arguments.map(arg => this.convertExpression(arg))
        )
    }

    convertPropertyAccessExpression(expression: ts.PropertyAccessExpression): Expression {
        return this.createPropertyAccessExpression(
            this.convertExpression(expression.expression),
            AstExpressionFactory.createIdentifier(expression.name.getText())
        )
    }

    convertElementAccessExpression(expression: ts.ElementAccessExpression): Expression {
        return this.createElementAccessExpression(
            this.convertExpression(expression.expression),
            this.convertExpression(expression.argumentExpression)
        )
    }

    convertNameExpression(name: ts.EntityName): Expression {
        return this.createNameExpression(
            this.convertEntityName(name)
        )
    }

    convertEntityName(entityName: ts.EntityName): NameEntity {
        if (ts.isQualifiedName(entityName)) {
            return AstExpressionFactory.createQualifierAsNameEntity(
                this.convertEntityName(entityName.left),
                this.convertEntityName(entityName.right).getIdentifier()
            )
        } else {
            return AstExpressionFactory.createIdentifierAsNameEntity(
                entityName.getText()
            )
        }
    }

    convertNumericLiteralExpression(literal: ts.NumericLiteral): Expression {
        return this.createNumericLiteralExpression(literal.getText())
    }

    convertBigIntLiteralExpression(literal: ts.BigIntLiteral): Expression {
        return this.createBigIntLiteralExpression(literal.getText())
    }

    convertStringLiteralExpression(literal: ts.StringLiteral): Expression {
        return this.createStringLiteralExpression(literal.getText())
    }

    private convertObjectProperty(name: ts.PropertyName, initializer: ts.Expression, optional: boolean): PropertyDeclaration | null {
        let convertedName = this.astConverter.convertName(name);

        if (convertedName) {
            return this.astConverter.createProperty(
                convertedName,
                this.convertExpression(initializer),
                this.astConverter.createTypeDeclaration("Unit"),
                [],
                optional
            )
        } else {
            return null;
        }
    }

    private convertObjectMethod(method: ts.MethodDeclaration): FunctionDeclaration | null {
        let convertedName = this.astConverter.convertName(method.name);

        if (convertedName) {
            return this.astConverter.createMethodDeclaration(
                convertedName,
                method.parameters.map((param, count) => this.astConverter.convertParameterDeclaration(param, count)),
                method.type ? this.astConverter.convertType(method.type) : this.astConverter.createTypeDeclaration("Unit"),
                this.astConverter.convertTypeParams(method.typeParameters),
                this.astConverter.convertModifiers(method.modifiers),
                this.astConverter.convertBlock(method.body),
            );
        } else {
            return null;
        }
    }

    convertObjectLiteralExpression(literal: ts.ObjectLiteralExpression): Expression {
        let members: Array<MemberDeclaration> = [];

        literal.properties.forEach(property => {
            let member: MemberDeclaration | null = null;

            if (ts.isPropertyAssignment(property)) {
                member = this.convertObjectProperty(property.name, property.initializer, !!property.questionToken);
            } else if (ts.isShorthandPropertyAssignment(property)) {
                member = this.convertObjectProperty(property.name, property.name, !!property.questionToken);
            } else if (ts.isMethodDeclaration(property)) {
                member = this.convertObjectMethod(property)
            } else if (ts.isSpreadAssignment(property)) {
                //TODO support spread assignments
            } else if (ts.isGetAccessorDeclaration(property) || ts.isSetAccessorDeclaration(property)) {
                //TODO support accessor declarations
            }

            if (member) {
                members.push(member);
            }
        });

        return this.createObjectLiteralExpression(members)
    }

    convertRegExLiteralExpression(literal: ts.RegularExpressionLiteral): Expression {
        return this.createRegExLiteralExpression(literal.getText())
    }

    convertLiteralExpression(expression: ts.LiteralExpression): Expression {
        if (ts.isNumericLiteral(expression)) {
            return this.convertNumericLiteralExpression(expression);
        } else if (ts.isBigIntLiteral(expression)) {
            return this.convertBigIntLiteralExpression(expression);
        } else if (ts.isStringLiteral(expression)) {
            return this.convertStringLiteralExpression(expression);
        } else if (ts.isRegularExpressionLiteral(expression)) {
            return this.convertRegExLiteralExpression(expression);
        } else {
            return this.convertUnknownExpression(expression)
        }
    }


    private convertToken(expression: ts.Expression): Expression {
        if (expression.kind == ts.SyntaxKind.TrueKeyword) {
            return this.createBooleanLiteralExpression(true)
        } else if (expression.kind == ts.SyntaxKind.FalseKeyword) {
            return this.createBooleanLiteralExpression(false)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }


    convertUnknownExpression(expression: ts.Expression): Expression {
        return this.createUnknownExpression(expression.getText())
    }


    convertExpression(expression: ts.Expression): Expression {
        if (ts.isBinaryExpression(expression)) {
            return this.convertBinaryExpression(expression);
        } else if (ts.isPrefixUnaryExpression(expression)) {
            return this.convertPrefixUnaryExpression(expression)
        } else if (ts.isPostfixUnaryExpression(expression)) {
            return this.convertPostfixUnaryExpression(expression)
        } else if (ts.isTypeOfExpression(expression)) {
            return this.convertTypeOfExpression(expression);
        } else if (ts.isCallExpression(expression)) {
            return this.convertCallExpression(expression);
        } else if (ts.isPropertyAccessExpression(expression)) {
            return this.convertPropertyAccessExpression(expression)
        } else if (ts.isElementAccessExpression(expression)) {
            return this.convertElementAccessExpression(expression)
        } else if (ts.isIdentifier(expression) || ts.isQualifiedName(expression)) {
            return this.convertNameExpression(expression);
        } else if (ts.isLiteralExpression(expression)) {
            return this.convertLiteralExpression(expression);
        } else if (ts.isObjectLiteralExpression(expression)) {
            return this.convertObjectLiteralExpression(expression);
        } else if (ts.isToken(expression)) {
            return this.convertToken(expression)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }
}