import * as ts from "typescript";
import {
    Block,
    Expression, HeritageClauseDeclaration,
    IdentifierDeclaration, LiteralExpression,
    MemberDeclaration, ModifierDeclaration,
    NameEntity, ParameterDeclaration,
    TemplateTokenDeclaration,
    TypeDeclaration, TypeParameter,
} from "./ast";
import {AstConverter} from "../AstConverter";
import {AstFactory} from "./AstFactory";
import * as declarations from "declarations";

export class AstExpressionConverter {
    constructor(
        private astConverter: AstConverter,
        private astFactory: AstFactory
    ) {
    }

    private asExpression(literalExpression: LiteralExpression): Expression {
        let expression = new declarations.ExpressionDeclarationProto();
        expression.setLiteralexpression(literalExpression);
        return expression;
    }

    createBinaryExpression(left: Expression, operator: string, right: Expression): Expression {
        let binaryExpression = new declarations.BinaryExpressionDeclarationProto();
        binaryExpression.setLeft(left);
        binaryExpression.setOperator(operator);
        binaryExpression.setRight(right);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setBinaryexpression(binaryExpression);
        return expression;
    }

    createUnaryExpression(operand: Expression, operator: string, isPrefix: boolean): Expression {
        let unaryExpression = new declarations.UnaryExpressionDeclarationProto();
        unaryExpression.setOperand(operand);
        unaryExpression.setOperator(operator);
        unaryExpression.setIsprefix(isPrefix);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setUnaryexpression(unaryExpression);
        return expression;
    }

    createFunctionExpression(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null, isGenerator: boolean) {
        let functionExpression = this.astFactory.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, body, [],"__NO_UID__", isGenerator);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setFunctionexpression(functionExpression);
        return expression;
    }

    createClassExpression(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>) {
        let classExpression = this.astFactory.createClassDeclaration(name, members, typeParams, parentEntities, modifiers, [], "__NO_UID__");

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setClassexpression(classExpression);
        return expression;
    }

    createTypeOfExpression(expression: Expression): Expression {
        let typeOfExpression = new declarations.TypeOfExpressionDeclarationProto();
        typeOfExpression.setExpression(expression);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setTypeofexpression(typeOfExpression);
        return expressionProto;
    }

    createCallExpression(expression: Expression, args: Array<Expression>, typeArguments: Array<TypeDeclaration>): Expression {
        let callExpression = new declarations.CallExpressionDeclarationProto();
        callExpression.setExpression(expression);
        callExpression.setArgumentsList(args);
        callExpression.setTypeargumentsList(typeArguments);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setCallexpression(callExpression);
        return expressionProto;
    }

    createPropertyAccessExpression(expression: Expression, name: IdentifierDeclaration): Expression {
        let propertyAccessExpression = new declarations.PropertyAccessExpressionDeclarationProto();
        propertyAccessExpression.setExpression(expression);
        propertyAccessExpression.setName(name);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setPropertyaccessexpression(propertyAccessExpression);
        return expressionProto;
    }

    createElementAccessExpression(expression: Expression, argumentExpression: Expression): Expression {
        let elementAccessExpression = new declarations.ElementAccessExpressionDeclarationProto();
        elementAccessExpression.setExpression(expression);
        elementAccessExpression.setArgumentexpression(argumentExpression);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setElementaccessexpression(elementAccessExpression);

        return expressionProto;
    }

    createNewExpression(expression: Expression, args: Array<Expression>, typeArguments: Array<TypeDeclaration>) {
        let newExpression = new declarations.NewExpressionDeclarationProto();
        newExpression.setExpression(expression);
        newExpression.setArgumentsList(args);
        newExpression.setTypeargumentsList(typeArguments);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setNewexpression(newExpression);
        return expressionProto;
    }

    createConditionalExpression(condition: Expression, whenTrue: Expression, whenFalse: Expression): Expression {
        let conditionalExpression = new declarations.ConditionalExpressionDeclarationProto();

        conditionalExpression.setCondition(condition);
        conditionalExpression.setWhentrue(whenTrue);
        conditionalExpression.setWhenfalse(whenFalse);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setConditionalexpression(conditionalExpression);

        return expressionProto;
    }

    createYieldExpression(expression: Expression | null, hasAsterisk: boolean) {
        let yieldExpression = new declarations.YieldExpressionDeclarationProto();

        if (expression) {
            yieldExpression.setExpression(expression)
        }
        yieldExpression.setHasasterisk(hasAsterisk);

        let expressionProto = new declarations.ExpressionDeclarationProto();
        expressionProto.setYieldexpression(yieldExpression);
        return expressionProto;
    }

    createNameExpression(name: NameEntity): Expression {
        let nameExpressionProto = new declarations.NameExpressionDeclarationProto();
        nameExpressionProto.setName(name);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setNameexpression(nameExpressionProto);

        return expression;
    }

    createNumericLiteralExpression(value: string): Expression {
        let numericLiteralExpression = new declarations.NumericLiteralExpressionDeclarationProto();
        numericLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setNumericliteral(numericLiteralExpression);
        return this.asExpression(literalExpression);
    }

    createBigIntLiteralExpression(value: string): Expression {
        let bigIntLiteralExpression = new declarations.BigIntLiteralExpressionDeclarationProto();
        bigIntLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setBigintliteral(bigIntLiteralExpression);
        return this.asExpression(literalExpression);
    }

    createStringLiteralExpression(value: string): Expression {
        let stringLiteralExpression = new declarations.StringLiteralExpressionDeclarationProto();
        stringLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setStringliteral(stringLiteralExpression);
        return this.asExpression(literalExpression);
    }

    createBooleanLiteralExpression(value: boolean): Expression {
        let booleanLiteralExpression = new declarations.BooleanLiteralExpressionDeclarationProto();
        booleanLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setBooleanliteral(booleanLiteralExpression);
        return this.asExpression(literalExpression);
    }

    createObjectLiteralExpression(members: Array<MemberDeclaration>): Expression {
        let objectLiteral = new declarations.ObjectLiteralDeclarationProto();
        objectLiteral.setMembersList(members);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setObjectliteral(objectLiteral);
        return this.asExpression(literalExpression);
    }

    createArrayLiteralExpression(elements: Array<Expression>): Expression {
        let arrayLiteral = new declarations.ArrayLiteralExpressionDeclarationProto();
        arrayLiteral.setElementsList(elements);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setArrayliteral(arrayLiteral);

        return this.asExpression(literalExpression);
    }

    createRegExLiteralExpression(value: string): Expression {
        let regExLiteralExpression = new declarations.RegExLiteralExpressionDeclarationProto();
        regExLiteralExpression.setValue(value);

        let literalExpression = new declarations.LiteralExpressionDeclarationProto();
        literalExpression.setRegexliteral(regExLiteralExpression);
        return this.asExpression(literalExpression);
    }

    createStringTemplateToken(value: string): TemplateTokenDeclaration {
        let stringLiteralExpression = new declarations.StringLiteralExpressionDeclarationProto();
        stringLiteralExpression.setValue(value);

        let templateToken = new declarations.TemplateTokenDeclarationProto();
        templateToken.setStringliteral(
          stringLiteralExpression
        );
        return templateToken;
    }

    createExpressionTemplateToken(expression: Expression): TemplateTokenDeclaration {
        let templateToken = new declarations.TemplateTokenDeclarationProto();
        templateToken.setExpression(expression);
        return templateToken;
    }

    createTemplateExpression(tokens: Array<TemplateTokenDeclaration>): Expression {
        let templateExpression = new declarations.TemplateExpressionDeclarationProto();
        templateExpression.setTokenList(tokens);
        let expression = new declarations.ExpressionDeclarationProto();
        expression.setTemplateexpression(templateExpression);
        return expression;
    }

    createAsExpression(subExpression: Expression, type: TypeDeclaration): Expression {
        let asExpression = new declarations.AsExpressionDeclarationProto();
        asExpression.setExpression(subExpression);
        asExpression.setType(type);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setAsexpression(asExpression);

        return expression;
    }

    createNonNullExpression(subExpression: Expression): Expression {
        let nonNullExpression = new declarations.NonNullExpressionDeclarationProto();
        nonNullExpression.setExpression(subExpression);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setNonnullexpression(nonNullExpression);

        return expression;
    }

    createParenthesizedExpression(subExpression: Expression): Expression {
        let nonNullExpression = new declarations.NonNullExpressionDeclarationProto();
        nonNullExpression.setExpression(subExpression);
        let expression = new declarations.ExpressionDeclarationProto();
        expression.setParenthesizedexpression(nonNullExpression);
        return expression;
    }

    createSpreadExpression(subExpression: Expression): Expression {
        let spreadExpression = new declarations.SpreadExpressionDeclarationProto();
        spreadExpression.setExpression(subExpression);
        let expression = new declarations.ExpressionDeclarationProto();
        expression.setSpreadexpression(spreadExpression);
        return expression;
    }

    createUnknownExpression(meta: string): Expression {
        let unknownExpression = new declarations.UnknownExpressionDeclarationProto();
        unknownExpression.setMeta(meta);

        let expression = new declarations.ExpressionDeclarationProto();
        expression.setUnknownexpression(unknownExpression);
        return expression;
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

    convertBody(body: ts.Node | null): Block | null {
        if (body) {
            if (ts.isBlock(body)) {
                return this.astConverter.convertBlock(body)
            } else {
                return this.astFactory.createBlockDeclaration(
                    [
                        this.astFactory.createReturnStatement(this.convertExpression(body))
                    ]
                )
            }
        } else {
            return null
        }
    }

    convertFunctionExpression(expression: ts.FunctionExpression): Expression {
        let name = expression.name ? expression.name.text : "";

        let parameterDeclarations = expression.parameters.map(
            (param, count) => this.astConverter.convertParameterDeclaration(param, count)
        );

        let returnType = expression.type ? this.astConverter.convertType(expression.type) : this.astConverter.createTypeDeclaration("Unit");

        let typeParameterDeclarations = this.astConverter.convertTypeParams(expression.typeParameters);

        return this.createFunctionExpression(
            name,
            parameterDeclarations,
            returnType,
            typeParameterDeclarations,
            this.astConverter.convertModifiers(expression.modifiers),
            this.convertBody(expression.body),
            expression.asteriskToken
        )
    }

    convertArrowFunctionExpression(expression: ts.ArrowFunction): Expression {
        return this.convertFunctionExpression(expression)
    }

    convertClassExpression(expression: ts.ClassExpression): Expression {
        return this.createClassExpression(
            this.astFactory.createIdentifierDeclarationAsNameEntity(""),
            this.astConverter.convertClassElementsToMembers(expression.members),
            this.astConverter.convertTypeParams(expression.typeParameters),
            this.astConverter.convertHeritageClauses(expression.heritageClauses, expression),
            this.astConverter.convertModifiers(expression.modifiers)
        );
    }

    convertTypeOfExpression(expression: ts.TypeOfExpression): Expression {
        return this.createTypeOfExpression(
            this.convertExpression(expression.expression),
        )
    }

    convertCallExpression(expression: ts.CallExpression): Expression {
        return this.createCallExpression(
            this.convertExpression(expression.expression),
            expression.arguments.map(arg => this.convertExpression(arg)),
            expression.typeArguments ?
                expression.typeArguments.map(arg => this.astConverter.convertType(arg)) : []
        )
    }

    convertPropertyAccessExpression(expression: ts.PropertyAccessExpression): Expression {
        let convertedExpression = this.convertExpression(expression.expression)
        let rightSideName = this.astFactory.createIdentifierDeclaration(expression.name.text)
        if (convertedExpression.hasNameexpression()) {
            let leftSideName = convertedExpression.getNameexpression()!.getName()!
            let newName = this.astFactory.createQualifiedNameEntity(leftSideName, rightSideName)
            return this.createNameExpression(newName)
        }

        return this.createPropertyAccessExpression(
            convertedExpression,
            rightSideName
        )
    }

    convertElementAccessExpression(expression: ts.ElementAccessExpression): Expression {
        return this.createElementAccessExpression(
            this.convertExpression(expression.expression),
            this.convertExpression(expression.argumentExpression)
        )
    }

    convertNewExpression(expression: ts.NewExpression): Expression {
        return this.createNewExpression(
            this.convertExpression(expression.expression),
            expression.arguments ?
                expression.arguments.map(arg => this.convertExpression(arg)) : [],
            expression.typeArguments ?
                expression.typeArguments.map(arg => this.astConverter.convertType(arg)) : []
        )
    }

    convertConditionalExpression(expression: ts.ConditionalExpression): Expression {
        return this.createConditionalExpression(
            this.convertExpression(expression.condition),
            this.convertExpression(expression.whenTrue),
            this.convertExpression(expression.whenFalse)
        )
    }

    convertYieldExpression(expression: ts.YieldExpression): Expression {
        return this.createYieldExpression(
            expression.expression ? this.convertExpression(expression.expression) : null,
            expression.asteriskToken
        )
    }

    convertNameExpression(name: ts.EntityName): Expression {
        return this.createNameExpression(
            this.convertEntityName(name)
        )
    }

    convertEntityName(entityName: ts.EntityName): NameEntity {
        if (ts.isQualifiedName(entityName)) {
            return this.astFactory.createQualifiedNameEntity(
                this.convertEntityName(entityName.left),
                this.convertEntityName(entityName.right).getIdentifier()!
            )
        } else {
            return this.astFactory.createIdentifierDeclarationAsNameEntity(
                entityName.text
            )
        }
    }

    convertNumericLiteralExpression(literal: ts.NumericLiteral): Expression {
        return this.createNumericLiteralExpression(literal.text)
    }

    convertBigIntLiteralExpression(literal: ts.BigIntLiteral): Expression {
        return this.createBigIntLiteralExpression(literal.text)
    }

    convertStringLiteralExpression(literal: ts.StringLiteral): Expression {
        return this.createStringLiteralExpression(literal.text)
    }

    private convertObjectProperty(name: ts.PropertyName, initializer: ts.Expression, optional: boolean): MemberDeclaration | null {
        let convertedName = this.astConverter.convertName(name);

        if (convertedName) {
            return this.astConverter.createProperty(
                convertedName,
                this.convertExpression(initializer),
                this.astConverter.createTypeDeclaration("Unit"),
                [],
                optional,
                true
            )
        } else {
            return null;
        }
    }

    private convertObjectMethod(method: ts.MethodDeclaration): MemberDeclaration | null {
        let convertedName = this.astConverter.convertName(method.name);

        if (convertedName) {
            return this.astConverter.createMethodDeclaration(
                convertedName,
                method.parameters.map((param, count) => this.astConverter.convertParameterDeclaration(param, count)),
                method.type ? this.astConverter.convertType(method.type) : this.astConverter.createTypeDeclaration("Unit"),
                this.astConverter.convertTypeParams(method.typeParameters),
                this.astConverter.convertModifiers(method.modifiers),
                this.astConverter.convertBlock(method.body),
                method.asteriskToken
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

    convertArrayLiteralExpression(literal: ts.ArrayLiteralExpression): Expression {
        return this.createArrayLiteralExpression(literal.elements.map((element) => this.convertExpression(element)))
    }

    convertRegExLiteralExpression(literal: ts.RegularExpressionLiteral): Expression {
        return this.createRegExLiteralExpression(literal.text)
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

    convertTemplateExpression(expression: ts.TemplateExpression): Expression {
        let tokens: Array<TemplateTokenDeclaration> = [];

        let head = expression.head.text;

        tokens.push(this.createStringTemplateToken(head));
        for (let span of expression.templateSpans) {
            if (ts.isTemplateMiddle(span.literal)) {
                let text = span.literal.text;

                tokens.push(this.createExpressionTemplateToken(
                    this.convertExpression(span.expression)
                ));
                tokens.push(this.createStringTemplateToken(text));
            } else if (ts.isTemplateTail(span.literal)) {
                let text = span.literal.text;

                tokens.push(this.createExpressionTemplateToken(
                    this.convertExpression(span.expression)
                ));
                tokens.push(this.createStringTemplateToken(text));
            }
        }
        return this.createTemplateExpression(tokens)
    }

    convertNoSubstitutionTemplateLiteral(literal: ts.NoSubstitutionTemplateLiteral): Expression {
        return this.createStringLiteralExpression(literal.text
            .split('`').join( '"'))
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

    private convertAsExpression(expression: ts.AssertionExpression): Expression {
        return this.createAsExpression(this.convertExpression(expression.expression), this.astConverter.convertType(expression.type))
    }

    private convertNonNullExpression(expression: ts.NonNullExpression): Expression {
        return this.createNonNullExpression(this.convertExpression(expression.expression))
    }

    private convertParenthesizedExpression(expression: ts.ParenthesizedExpression): Expression {
        return this.createParenthesizedExpression(this.convertExpression(expression.expression))
    }

    private convertSpreadExpression(expression: ts.SpreadElement): Expression {
        return this.createSpreadExpression(this.convertExpression(expression.expression))
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
        } else if (ts.isFunctionExpression(expression)) {
            return this.convertFunctionExpression(expression)
        } else if (ts.isArrowFunction(expression)) {
            return this.convertArrowFunctionExpression(expression)
        } else if (ts.isClassExpression(expression)) {
            return this.convertClassExpression(expression)
        } else if (ts.isTypeOfExpression(expression)) {
            return this.convertTypeOfExpression(expression);
        } else if (ts.isCallExpression(expression)) {
            return this.convertCallExpression(expression);
        } else if (ts.isPropertyAccessExpression(expression)) {
            return this.convertPropertyAccessExpression(expression)
        } else if (ts.isElementAccessExpression(expression)) {
            return this.convertElementAccessExpression(expression)
        } else if (ts.isNewExpression(expression)) {
            return this.convertNewExpression(expression)
        } else if (ts.isIdentifier(expression) || ts.isQualifiedName(expression)) {
            return this.convertNameExpression(expression);
        } else if (ts.isTemplateExpression(expression)) {
            return this.convertTemplateExpression(expression)
        } else if (ts.isNoSubstitutionTemplateLiteral(expression)) {
            return this.convertNoSubstitutionTemplateLiteral(expression)
        } else if (ts.isLiteralExpression(expression)) {
            return this.convertLiteralExpression(expression);
        } else if (ts.isObjectLiteralExpression(expression)) {
            return this.convertObjectLiteralExpression(expression);
        } else if (ts.isArrayLiteralExpression(expression)) {
            return this.convertArrayLiteralExpression(expression);
        } else if (ts.isConditionalExpression(expression)) {
            return this.convertConditionalExpression(expression);
        } else if (ts.isYieldExpression(expression)) {
            return this.convertYieldExpression(expression);
        } else if (ts.isToken(expression)) {
            return this.convertToken(expression)
        } else if (ts.isAssertionExpression(expression)) {
            return this.convertAsExpression(expression)
        } else if (ts.isNonNullExpression(expression)) {
            return this.convertNonNullExpression(expression)
        } else if (ts.isParenthesizedExpression(expression)) {
            return this.convertParenthesizedExpression(expression)
        } else if (ts.isSpreadElement(expression)) {
            return this.convertSpreadExpression(expression)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }
}