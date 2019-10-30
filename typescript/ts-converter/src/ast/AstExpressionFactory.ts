import * as declarations from "declarations";
import {
    Expression,
    LiteralExpression
} from "./ast";

export class AstExpressionFactory {
    static createIdentifierExpressionDeclarationAsExpression(value: string): Expression {
        let identifier = new declarations.IdentifierEntityProto();
        identifier.setValue(value);

        let identifierExpressionProto = new declarations.IdentifierExpressionDeclarationProto();
        identifierExpressionProto.setIdentifier(identifier);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setIdentifierexpression(identifierExpressionProto);
        return expression;
    }

    static createBinaryExpressionDeclarationAsExpression(left: Expression, operator: string, right: Expression): Expression {
        let binaryExpression = new declarations.BinaryExpressionDeclarationProto();
        binaryExpression.setLeft(left);
        binaryExpression.setOperator(operator);
        binaryExpression.setRight(right);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setBinaryexpression(binaryExpression);
        return expression;
    }

    static createUnknownExpressionDeclarationAsExpression(meta: string): Expression {
        let unknownExpression = new declarations.UnknownExpressionDeclarationProto();
        unknownExpression.setMeta(meta);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setUnknownexpression(unknownExpression);
        return expression;
    }

    private static asExpression(literalExpression: LiteralExpression): Expression {
        let expression = new declarations.ExpressionDeclarationProto();
        expression.setLiteralexpression(literalExpression);
        return expression;
    }

    static createNumericLiteralDeclarationAsExpression(value: string): Expression {
        let numericLiteralExpression = new declarations.NumericLiteralExpressionDeclarationProto();
        numericLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setNumericliteral(numericLiteralExpression);
        return this.asExpression(literalExpression);
    }

    static createBigIntLiteralDeclarationAsExpression(value: string): Expression {
        let bigIntLiteralExpression = new declarations.BigIntLiteralExpressionDeclarationProto();
        bigIntLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setBigintliteral(bigIntLiteralExpression);
        return this.asExpression(literalExpression);
    }

    static createStringLiteralDeclarationAsExpression(value: string): Expression {
        let stringLiteralExpression = new declarations.StringLiteralExpressionDeclarationProto();
        stringLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setStringliteral(stringLiteralExpression);
        return this.asExpression(literalExpression);
    }

    static createRegExLiteralDeclarationAsExpression(value: string): Expression {
        let regExLiteralExpression = new declarations.RegExLiteralExpressionDeclarationProto();
        regExLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setRegExliteral(regExLiteralExpression);
        return this.asExpression(literalExpression);
    }
}