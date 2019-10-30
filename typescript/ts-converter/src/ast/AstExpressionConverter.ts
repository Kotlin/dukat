import * as ts from "typescript-services-api";
import {Expression} from "./ast";
import {AstExpressionFactory} from "./AstExpressionFactory";

export class AstExpressionConverter {
    static createIdentifierExpression(name: string): Expression {
        return AstExpressionFactory.createIdentifierExpressionDeclarationAsExpression(name);
    }

    static createBinaryExpression(left: Expression, operator: string, right: Expression): Expression {
        return AstExpressionFactory.createBinaryExpressionDeclarationAsExpression(left, operator, right);
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

    static createRegExLiteralExpression(value: string): Expression {
        return AstExpressionFactory.createRegExLiteralDeclarationAsExpression(value);
    }

    static createUnknownExpression(value: string): Expression {
        return AstExpressionFactory.createUnknownExpressionDeclarationAsExpression(value);
    }

    static convertIdentifierExpression(expression: ts.Expression): Expression {
        return this.createIdentifierExpression(expression.getText())
    }

    static convertBinaryExpression(expression: ts.Expression): Expression {
        return this.createBinaryExpression(
            this.convertExpression(expression.left),
            ts.tokenToString(expression.operatorToken.kind),
            this.convertExpression(expression.right)
        )
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

    static convertUnknownExpression(expression: ts.Expression): Expression {
        return this.createUnknownExpression(expression.getText())
    }

    static convertExpression(expression: ts.Expression): Expression {
        if (ts.isBinaryExpression(expression)) {
            return this.convertBinaryExpression(expression);
        } else if (ts.isIdentifier(expression)) {
            return this.convertIdentifierExpression(expression);
        } else if (ts.isNumericLiteral(expression)) {
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
}