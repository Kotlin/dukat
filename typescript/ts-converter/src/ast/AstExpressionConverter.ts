import * as ts from "typescript";
import {
    Block,
    Expression, HeritageClauseDeclaration,
    IdentifierDeclaration,
    MemberDeclaration, ModifierDeclaration,
    NameEntity, ParameterDeclaration, TypeDeclaration, TypeParameter,
} from "./ast";
import {AstExpressionFactory} from "./AstExpressionFactory";
import {AstConverter} from "../AstConverter";
import {AstFactory} from "./AstFactory";

export class AstExpressionConverter {
    constructor(
        private astConverter: AstConverter,
        private astFactory: AstFactory
    ) {
    }

    createBinaryExpression(left: Expression, operator: string, right: Expression): Expression {
        return AstExpressionFactory.createBinaryExpressionDeclarationAsExpression(left, operator, right);
    }

    createUnaryExpression(operand: Expression, operator: string, isPrefix: boolean) {
        return AstExpressionFactory.createUnaryExpressionDeclarationAsExpression(operand, operator, isPrefix);
    }

    createFunctionExpression(name: string, parameters: Array<ParameterDeclaration>, type: TypeDeclaration, typeParams: Array<TypeParameter>, modifiers: Array<ModifierDeclaration>, body: Block | null) {
        return AstExpressionFactory.convertFunctionDeclarationToExpression(this.astFactory.createFunctionDeclaration(name, parameters, type, typeParams, modifiers, body, "__NO_UID__"));
    }

    createClassExpression(name: NameEntity, members: Array<MemberDeclaration>, typeParams: Array<TypeParameter>, parentEntities: Array<HeritageClauseDeclaration>, modifiers: Array<ModifierDeclaration>) {
        return AstExpressionFactory.convertClassDeclarationToExpression(this.astFactory.createClassDeclaration(name, members, typeParams, parentEntities, modifiers, "__NO_UID__"));
    }

    createTypeOfExpression(expression: Expression) {
        return AstExpressionFactory.createTypeOfExpressionDeclarationAsExpression(expression);
    }

    createCallExpression(expression: Expression, args: Array<Expression>) {
        return AstExpressionFactory.createCallExpressionDeclarationAsExpression(expression, args);
    }

    createPropertyAccessExpression(expression: Expression, name: IdentifierDeclaration) {
        return AstExpressionFactory.createPropertyAccessExpressionDeclarationAsExpression(expression, name);
    }

    createElementAccessExpression(expression: Expression, argumentExpression: Expression) {
        return AstExpressionFactory.createElementAccessExpressionDeclarationAsExpression(expression, argumentExpression);
    }

    createNewExpression(expression: Expression, args: Array<Expression>) {
        return AstExpressionFactory.createNewExpressionDeclarationAsExpression(expression, args);
    }

    createConditionalExpression(condition: Expression, whenTrue: Expression, whenFalse: Expression) {
        return AstExpressionFactory.createConditionalExpressionDeclarationAsExpression(condition, whenTrue, whenFalse);
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

    createArrayLiteralExpression(elements: Array<Expression>): Expression {
        return AstExpressionFactory.createArrayLiteralDeclarationAsExpression(elements);
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
        let name = expression.name ? expression.name.getText() : "";

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
            this.convertBody(expression.body)
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

    convertNewExpression(expression: ts.NewExpression): Expression {
        return this.createNewExpression(
            this.convertExpression(expression.expression),
            expression.arguments ?
                expression.arguments.map(arg => this.convertExpression(arg)) : []
        )
    }

    convertConditionalExpression(expression: ts.ConditionalExpression): Expression {
        return this.createConditionalExpression(
            this.convertExpression(expression.condition),
            this.convertExpression(expression.whenTrue),
            this.convertExpression(expression.whenFalse)
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
                this.convertEntityName(entityName.right).getIdentifier()!
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

    private convertObjectProperty(name: ts.PropertyName, initializer: ts.Expression, optional: boolean): MemberDeclaration | null {
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
        } else if (ts.isLiteralExpression(expression)) {
            return this.convertLiteralExpression(expression);
        } else if (ts.isObjectLiteralExpression(expression)) {
            return this.convertObjectLiteralExpression(expression);
        } else if (ts.isArrayLiteralExpression(expression)) {
            return this.convertArrayLiteralExpression(expression);
        } else if (ts.isConditionalExpression(expression)) {
            return this.convertConditionalExpression(expression);
        } else if (ts.isToken(expression)) {
            return this.convertToken(expression)
        } else {
            return this.convertUnknownExpression(expression)
        }
    }
}