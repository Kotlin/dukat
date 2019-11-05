import * as ts from "typescript-services-api";
import {Expression, IdentifierEntity, NameEntity} from "./ast";
import {AstExpressionFactory} from "./AstExpressionFactory";

export class AstExpressionConverter {
    static createBinaryExpression(left: Expression, operator: string, right: Expression): Expression {
        return AstExpressionFactory.createBinaryExpressionDeclarationAsExpression(left, operator, right);
    }

    static createUnaryExpression(operand: Expression, operator: string, isPrefix: boolean) {
        return AstExpressionFactory.createUnaryExpressionDeclarationAsExpression(operand, operator, isPrefix);
    }

    static createPropertyAccessExpression(expression: Expression, name: IdentifierEntity) {
        return AstExpressionFactory.createPropertyAccessExpressionDeclarationAsExpression(expression, name);
    }

    static createElementAccessExpression(expression: Expression, argumentExpression: Expression) {
        return AstExpressionFactory.createElementAccessExpressionDeclarationAsExpression(expression, argumentExpression);
    }

    static createNameExpression(name: NameEntity): Expression {
        return AstExpressionFactory.createNameExpressionDeclarationAsExpression(name)
    }

    static createNumericLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createNumericLiteralDeclarationAsExpression(value);
    }

    static createBigIntLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createBigIntLiteralDeclarationAsExpression(value);
    }

    static createStringLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createStringLiteralDeclarationAsExpression(value);
    }

    static createBooleanLiteralExpression(value: boolean): Expression {
        return AstExpressionFactory.createBooleanLiteralDeclarationAsExpression(value);
    }

    static createRegExLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createRegExLiteralDeclarationAsExpression(value);
    }

    static createUnknownExpression(value: string): Expression {
        return AstExpressionFactory.createUnknownExpressionDeclarationAsExpression(value);
    }


    static convertBinaryExpression(expression: ts.BinaryExpression): Expression {
        return this.createBinaryExpression(
            this.convertExpression(expression.left),
            ts.tokenToString(expression.operatorToken.kind),
            this.convertExpression(expression.right)
        )
    }

    static convertPrefixUnaryExpression(expression: ts.PrefixUnaryExpression): Expression {
        return this.createUnaryExpression(
            this.convertExpression(expression.operand),
            ts.tokenToString(expression.operator),
            true
        )
    }

    static convertPostfixUnaryExpression(expression: ts.PostfixUnaryExpression): Expression {
        return this.createUnaryExpression(
            this.convertExpression(expression.operand),
            ts.tokenToString(expression.operator),
            false
        )
    }

    static convertPropertyAccessExpression(expression: ts.PropertyAccessExpression): Expression {
        return this.createPropertyAccessExpression(
            this.convertExpression(expression.expression),
            AstExpressionFactory.createIdentifier(expression.name.getText())
        )
    }

    static convertElementAccessExpression(expression: ts.ElementAccessExpression): Expression {
        return this.createElementAccessExpression(
            this.convertExpression(expression.expression),
            this.convertExpression(expression.argumentExpression)
        )
    }

    static convertNameExpression(name: ts.EntityName): Expression {
        return this.createNameExpression(
            this.convertEntityName(name)
        )
    }

    static convertEntityName(entityName: ts.EntityName): NameEntity {
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

    static convertNumericLiteralExpression(expression: ts.Expression): Expression {
        return this.createNumericLiteralExpression(expression.getText())
    }

    static convertBigIntLiteralExpression(expression: ts.Expression): Expression {
        return this.createBigIntLiteralExpression(expression.getText())
    }

    static convertStringLiteralExpression(expression: ts.Expression): Expression {
        return this.createStringLiteralExpression(expression.getText())
    }

    static convertRegExLiteralExpression(expression: ts.Expression): Expression {
        return this.createRegExLiteralExpression(expression.getText())
    }

    static convertLiteralExpression(expression: ts.LiteralExpression): Expression {
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


    static convertToken(expression: ts.Expression): Expression {
        if (expression.kind == ts.SyntaxKind.TrueKeyword) {
            return this.createBooleanLiteralExpression(true)
        } else if (expression.kind == ts.SyntaxKind.FalseKeyword) {
            return this.createBooleanLiteralExpression(false)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }


    static convertUnknownExpression(expression: ts.Expression): Expression {
        return this.createUnknownExpression(expression.getText())
    }


    static convertExpression(expression: ts.Expression): Expression {
        if (ts.isBinaryExpression(expression)) {
            return this.convertBinaryExpression(expression);
        } else if (ts.isPrefixUnaryExpression(expression)) {
            return this.convertPrefixUnaryExpression(expression)
        } else if (ts.isPostfixUnaryExpression(expression)) {
            return this.convertPostfixUnaryExpression(expression)
        } else if (ts.isPropertyAccessExpression(expression)) {
            return this.convertPropertyAccessExpression(expression)
        } else if (ts.isElementAccessExpression(expression)) {
            return this.convertElementAccessExpression(expression)
        } else if (ts.isIdentifier(expression) || ts.isQualifiedName(expression)) {
            return this.convertNameExpression(expression);
        } else if (ts.isLiteralExpression(expression)) {
            return this.convertLiteralExpression(expression);
        } else if (ts.isToken(expression)) {
            return this.convertToken(expression)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }
}