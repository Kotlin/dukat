import * as declarations from "declarations";
import {
    Block,
    Expression,
    IdentifierEntity,
    LiteralExpression,
    MemberDeclaration,
    ModifierDeclaration,
    NameEntity,
    ObjectMember,
    ParameterDeclaration,
    ParameterValue, ProtoMessage,
    TypeParameter
} from "./ast";

export class AstExpressionFactory {
    static createBinaryExpressionDeclarationAsExpression(left: Expression, operator: string, right: Expression): Expression {
        let binaryExpression = new declarations.BinaryExpressionDeclarationProto();
        binaryExpression.setLeft(left);
        binaryExpression.setOperator(operator);
        binaryExpression.setRight(right);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setBinaryexpression(binaryExpression);
        return expression;
    }

    static createUnaryExpressionDeclarationAsExpression(operand: Expression, operator: string, isPrefix: boolean): Expression {
        let unaryExpression = new declarations.UnaryExpressionDeclarationProto();
        unaryExpression.setOperand(operand);
        unaryExpression.setOperator(operator);
        unaryExpression.setIsprefix(isPrefix);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setUnaryexpression(unaryExpression);
        return expression;
    }

    static createTypeOfExpressionDeclarationAsExpression(expression: Expression): Expression {
        let typeOfExpression = new declarations.TypeOfExpressionDeclarationProto();
        typeOfExpression.setExpression(expression);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setTypeofexpression(typeOfExpression);
        return expressionProto;
    }

    static createCallExpressionDeclarationAsExpression(expression: Expression, args: Array<Expression>): Expression {
        let callExpression = new declarations.CallExpressionDeclarationProto();
        callExpression.setExpression(expression);
        callExpression.setArgumentsList(args);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setCallexpression(callExpression);
        return expressionProto;
    }

    static createPropertyAccessExpressionDeclarationAsExpression(expression: Expression, name: IdentifierEntity): Expression {
        let propertyAccessExpression = new declarations.PropertyAccessExpressionDeclarationProto();
        propertyAccessExpression.setExpression(expression);
        propertyAccessExpression.setName(name);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setPropertyaccessexpression(propertyAccessExpression);
        return expressionProto;
    }

    static createElementAccessExpressionDeclarationAsExpression(expression: Expression, argumentExpression: Expression): Expression {
        let elementAccessExpression = new declarations.ElementAccessExpressionDeclarationProto();
        elementAccessExpression.setExpression(expression);
        elementAccessExpression.setArgumentexpression(argumentExpression);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setElementaccessexpression(elementAccessExpression);
        return expressionProto;
    }

    static createQualifierAsNameEntity(left: NameEntity, right: IdentifierEntity): NameEntity {
        let qualifier = new declarations.QualifierEntityProto();
        qualifier.setLeft(left);
        qualifier.setRight(right);

        let nameEntity = new declarations.NameEntityProto();
        nameEntity.setQualifier(qualifier);
        return nameEntity;
    }

    static createIdentifier(value: string): IdentifierEntity {
        let identifierProto = new declarations.IdentifierEntityProto();
        identifierProto.setValue(value);
        return identifierProto;
    }

    static createIdentifierAsNameEntity(value: string): NameEntity {
        let nameEntity = new declarations.NameEntityProto();
        nameEntity.setIdentifier(this.createIdentifier(value));
        return nameEntity;
    }

    static createNameExpressionDeclarationAsExpression(name: NameEntity): Expression {
        let nameExpressionProto = new declarations.NameExpressionDeclarationProto();
        nameExpressionProto.setName(name);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setNameexpression(nameExpressionProto);
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

    static createBooleanLiteralDeclarationAsExpression(value: boolean): Expression {
        let booleanLiteralExpression = new declarations.BooleanLiteralExpressionDeclarationProto();
        booleanLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setBooleanliteral(booleanLiteralExpression);
        return this.asExpression(literalExpression);
    }

    static createObjectLiteralDeclarationAsExpression(members: Array<ObjectMember>): Expression {
        let objectLiteral = new declarations.ObjectLiteralDeclarationProto();
        objectLiteral.setMembersList(members);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setObjectliteral(objectLiteral);
        return this.asExpression(literalExpression);
    }

    static createRegExLiteralDeclarationAsExpression(value: string): Expression {
        let regExLiteralExpression = new declarations.RegExLiteralExpressionDeclarationProto();
        regExLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setRegexliteral(regExLiteralExpression);
        return this.asExpression(literalExpression);
    }

    static createObjectProperty(name: string, initializer: Expression | null): ObjectMember {
        let objectProperty = new declarations.ObjectPropertyDeclarationProto();
        objectProperty.setName(name);
        if(initializer) {
            objectProperty.setInitializer(initializer);
        }

        let objectMember = new declarations.ObjectMemberDeclarationProto();
        objectMember.setObjectproperty(objectProperty);
        return objectMember;
    }

    static createObjectMethod(name: string, parameters: Array<ParameterDeclaration>, type: ParameterValue, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null): ObjectMember {
        let functionDeclaration = new declarations.FunctionDeclarationProto();
        functionDeclaration.setName(name);
        functionDeclaration.setParametersList(parameters);
        functionDeclaration.setType(type);
        functionDeclaration.setTypeparametersList(typeParams);
        functionDeclaration.setModifiersList(modifiers);
        if (body) {
            functionDeclaration.setBody(body);
        }
        functionDeclaration.setUid("__NO_UID__");

        let objectMember = new declarations.ObjectMemberDeclarationProto();
        objectMember.setFunctiondeclaration(functionDeclaration);
        return objectMember;
    }
}