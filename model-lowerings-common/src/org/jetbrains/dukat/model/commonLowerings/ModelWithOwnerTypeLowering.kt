package org.jetbrains.dukat.model.commonLowerings

import org.jetbrains.dukat.astModel.ClassLikeModel
import org.jetbrains.dukat.astModel.ClassModel
import org.jetbrains.dukat.astModel.ConstructorModel
import org.jetbrains.dukat.astModel.EnumModel
import org.jetbrains.dukat.astModel.FunctionModel
import org.jetbrains.dukat.astModel.FunctionTypeModel
import org.jetbrains.dukat.astModel.HeritageModel
import org.jetbrains.dukat.astModel.InterfaceModel
import org.jetbrains.dukat.astModel.MemberModel
import org.jetbrains.dukat.astModel.MethodModel
import org.jetbrains.dukat.astModel.ModuleModel
import org.jetbrains.dukat.astModel.ObjectModel
import org.jetbrains.dukat.astModel.ParameterModel
import org.jetbrains.dukat.astModel.PropertyModel
import org.jetbrains.dukat.astModel.TypeAliasModel
import org.jetbrains.dukat.astModel.VariableModel
import org.jetbrains.dukat.astModel.LambdaParameterModel
import org.jetbrains.dukat.astModel.expressions.AsExpressionModel
import org.jetbrains.dukat.astModel.expressions.BinaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.CallExpressionModel
import org.jetbrains.dukat.astModel.expressions.ConditionalExpressionModel
import org.jetbrains.dukat.astModel.expressions.ExpressionModel
import org.jetbrains.dukat.astModel.expressions.IdentifierExpressionModel
import org.jetbrains.dukat.astModel.expressions.IndexExpressionModel
import org.jetbrains.dukat.astModel.expressions.NonNullExpressionModel
import org.jetbrains.dukat.astModel.expressions.PropertyAccessExpressionModel
import org.jetbrains.dukat.astModel.expressions.SuperExpressionModel
import org.jetbrains.dukat.astModel.expressions.ThisExpressionModel
import org.jetbrains.dukat.astModel.expressions.UnaryExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.BooleanLiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.literals.LiteralExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.ExpressionTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.StringTemplateTokenModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateExpressionModel
import org.jetbrains.dukat.astModel.expressions.templates.TemplateTokenModel
import org.jetbrains.dukat.astModel.statements.AssignmentStatementModel
import org.jetbrains.dukat.astModel.statements.BlockStatementModel
import org.jetbrains.dukat.astModel.statements.BreakStatementModel
import org.jetbrains.dukat.astModel.statements.CaseModel
import org.jetbrains.dukat.astModel.statements.ContinueStatementModel
import org.jetbrains.dukat.astModel.statements.ExpressionStatementModel
import org.jetbrains.dukat.astModel.statements.IfStatementModel
import org.jetbrains.dukat.astModel.statements.ReturnStatementModel
import org.jetbrains.dukat.astModel.statements.RunBlockStatementModel
import org.jetbrains.dukat.astModel.statements.StatementModel
import org.jetbrains.dukat.astModel.statements.WhenStatementModel
import org.jetbrains.dukat.astModel.statements.WhileStatementModel
import org.jetbrains.dukat.logger.Logging
import org.jetbrains.dukat.ownerContext.NodeOwner


private val logger = Logging.logger("introduceModels")

interface ModelWithOwnerTypeLowering : ModelWithOwnerLowering {

    override fun lowerEnumModel(ownerContext: NodeOwner<EnumModel>, parentModule: ModuleModel): EnumModel {
        return ownerContext.node
    }

    fun lowerMethodModel(ownerContext: NodeOwner<MethodModel>): MethodModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) },
                body = declaration.body?.let { lowerStatementBody(NodeOwner(it, ownerContext)) }
        )
    }

    fun lowerPropertyModel(ownerContext: NodeOwner<PropertyModel>): PropertyModel {
        val declaration = ownerContext.node
        return declaration.copy(
            type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
            typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) }
        )
    }

    override fun lowerMemberModel(ownerContext: NodeOwner<MemberModel>, parentModule: ModuleModel): MemberModel? {
        return when (val declaration = ownerContext.node) {
            is MethodModel -> lowerMethodModel(NodeOwner(declaration, ownerContext))
            is PropertyModel -> lowerPropertyModel(NodeOwner(declaration, ownerContext))
            is ConstructorModel -> lowerConstructorModel(NodeOwner(declaration, ownerContext))
            is ClassLikeModel -> lowerClassLikeModel(NodeOwner(declaration, ownerContext), parentModule)
            else -> {
                logger.trace("skipping $declaration")
                declaration
            }
        }
    }

    override fun lowerFunctionModel(ownerContext: NodeOwner<FunctionModel>, parentModule: ModuleModel): FunctionModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
                typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) },
                body = lowerStatementBody(NodeOwner(declaration.body, ownerContext))
        )
    }

    override fun lowerFunctionTypeModel(ownerContext: NodeOwner<FunctionTypeModel>): FunctionTypeModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerLambdaParameterModel(NodeOwner(parameter, ownerContext)) },
                type = lowerTypeModel(NodeOwner(declaration.type, ownerContext))
        )
    }

    override fun lowerLambdaParameterModel(ownerContext: NodeOwner<LambdaParameterModel>): LambdaParameterModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)))
    }

    override fun lowerParameterModel(ownerContext: NodeOwner<ParameterModel>): ParameterModel {
        val declaration = ownerContext.node
        return declaration.copy(type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)))
    }

    override fun lowerVariableModel(ownerContext: NodeOwner<VariableModel>, parentModule: ModuleModel?): VariableModel {
        val declaration = ownerContext.node
        return declaration.copy(
            type = lowerTypeModel(NodeOwner(declaration.type, ownerContext)),
            typeParameters = declaration.typeParameters.map { lowerTypeParameterModel(ownerContext.wrap(it)) },
            get = declaration.get?.let { lowerStatementModel(NodeOwner(it, ownerContext)) },
            set = declaration.set?.let { lowerStatementModel(NodeOwner(it, ownerContext)) },
            initializer = declaration.initializer?.let { lowerExpressionModel(NodeOwner(it, ownerContext)) }
        )
    }

    fun lowerHeritageModel(ownerContext: NodeOwner<HeritageModel>): HeritageModel {
        val heritageClause = ownerContext.node
        val typeParams = heritageClause.typeParams.map {
            lowerTypeModel(ownerContext.wrap(it))
        }
        return heritageClause.copy(
            value = lowerTypeValueModel(ownerContext.wrap(heritageClause.value)),
            typeParams = typeParams
        )
    }


    override fun lowerInterfaceModel(ownerContext: NodeOwner<InterfaceModel>, parentModule: ModuleModel): InterfaceModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.mapNotNull { member -> lowerMemberModel(NodeOwner(member, ownerContext), parentModule) },
                typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageModel(NodeOwner(heritageClause, ownerContext))
                }
        )
    }

    override fun lowerTypeAliasModel(ownerContext: NodeOwner<TypeAliasModel>, parentModule: ModuleModel): TypeAliasModel {
        val declaration = ownerContext.node
        return declaration.copy(
            typeReference = lowerTypeModel(ownerContext.wrap(declaration.typeReference)),
            typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) }
        )
    }

    fun lowerConstructorModel(ownerContext: NodeOwner<ConstructorModel>): ConstructorModel {
        val declaration = ownerContext.node
        return declaration.copy(
                parameters = declaration.parameters.map { parameter -> lowerParameterModel(NodeOwner(parameter, ownerContext)) }
        )
    }

    override fun lowerObjectModel(ownerContext: NodeOwner<ObjectModel>, parentModule: ModuleModel): ObjectModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.mapNotNull { member -> lowerMemberModel(NodeOwner(member, ownerContext), parentModule) }
        )

    }

    override fun lowerClassModel(ownerContext: NodeOwner<ClassModel>, parentModule: ModuleModel): ClassModel {
        val declaration = ownerContext.node
        return declaration.copy(
                members = declaration.members.mapNotNull { member -> lowerMemberModel(NodeOwner(member, ownerContext), parentModule) },
                typeParameters = declaration.typeParameters.map { typeParameterModel ->  lowerTypeParameterModel(ownerContext.wrap(typeParameterModel)) },
                parentEntities = declaration.parentEntities.map { heritageClause ->
                    lowerHeritageModel(NodeOwner(heritageClause, ownerContext))
                },
                companionObject = declaration.companionObject?.let { lowerObjectModel(ownerContext.wrap(it), parentModule) }
        )
    }

    override fun lowerStatementModel(ownerContext: NodeOwner<StatementModel>): StatementModel {
        return when (val statement = ownerContext.node) {
            is AssignmentStatementModel -> lowerAssignmentStatementModel(NodeOwner(statement, ownerContext))
            is BlockStatementModel -> lowerStatementBody(NodeOwner(statement, ownerContext))
            is BreakStatementModel -> ownerContext.node
            is ContinueStatementModel -> ownerContext.node
            is ExpressionStatementModel -> lowerExpressionStatementModel(NodeOwner(statement, ownerContext))
            is IfStatementModel -> lowerIfStatementModel(NodeOwner(statement, ownerContext))
            is ReturnStatementModel -> lowerReturnStatementModel(NodeOwner(statement, ownerContext))
            is RunBlockStatementModel -> lowerRunBlockStatementModel(NodeOwner(statement, ownerContext))
            is VariableModel -> lowerVariableModel(NodeOwner(statement, ownerContext), null)
            is WhenStatementModel -> lowerWhenStatementModel(NodeOwner(statement, ownerContext))
            is WhileStatementModel -> lowerWhileStatementModel(NodeOwner(statement, ownerContext))
            else -> {
                logger.trace("skipping $statement")
                statement
            }
        }
    }

    fun lowerAssignmentStatementModel(ownerContext: NodeOwner<AssignmentStatementModel>) : AssignmentStatementModel {
        return ownerContext.node.copy(
            left = lowerExpressionModel(NodeOwner(ownerContext.node.left, ownerContext)),
            right = lowerExpressionModel(NodeOwner(ownerContext.node.right, ownerContext))
        )
    }

    fun lowerExpressionStatementModel(ownerContext: NodeOwner<ExpressionStatementModel>) : ExpressionStatementModel {
        return ownerContext.node.copy(
            expression = lowerExpressionModel(NodeOwner(ownerContext.node.expression, ownerContext))
        )
    }

    fun lowerIfStatementModel(ownerContext: NodeOwner<IfStatementModel>) : IfStatementModel {
        return ownerContext.node.copy(
            condition = lowerExpressionModel(NodeOwner(ownerContext.node.condition, ownerContext)),
            thenStatement = lowerStatementBody(NodeOwner(ownerContext.node.thenStatement, ownerContext)),
            elseStatement = ownerContext.node.elseStatement?.let {
                lowerStatementBody(NodeOwner(it, ownerContext))
            }
        )
    }

    fun lowerReturnStatementModel(ownerContext: NodeOwner<ReturnStatementModel>) : ReturnStatementModel {
        return ownerContext.node.copy(
            expression = ownerContext.node.expression?.let {
                lowerExpressionModel(NodeOwner(it, ownerContext))
            }
        )
    }

    fun lowerRunBlockStatementModel(ownerContext: NodeOwner<RunBlockStatementModel>) : RunBlockStatementModel {
        return ownerContext.node.copy(statements = ownerContext.node.statements.map {
            lowerStatementModel(NodeOwner(it, ownerContext))
        })
    }

    fun lowerWhenStatementModel(ownerContext: NodeOwner<WhenStatementModel>) : WhenStatementModel {
        return ownerContext.node.copy(
            expression = lowerExpressionModel(NodeOwner(ownerContext.node.expression, ownerContext)),
            cases = ownerContext.node.cases.map {
                lowerCaseModel(NodeOwner(it, ownerContext))
            }
        )
    }

    fun lowerCaseModel(ownerContext: NodeOwner<CaseModel>) : CaseModel {
        return ownerContext.node.copy(
            condition = ownerContext.node.condition?.map {
                lowerExpressionModel(NodeOwner(it, ownerContext))
            },
            body = lowerStatementBody(NodeOwner(ownerContext.node.body, ownerContext))
        )
    }

    fun lowerWhileStatementModel(ownerContext: NodeOwner<WhileStatementModel>) : WhileStatementModel {
        return ownerContext.node.copy(
            condition = lowerExpressionModel(NodeOwner(ownerContext.node.condition, ownerContext)),
            body = lowerStatementBody(NodeOwner(ownerContext.node.body, ownerContext))
        )
    }

    fun lowerExpressionModel(ownerContext: NodeOwner<ExpressionModel>) : ExpressionModel {
        return when (val expression = ownerContext.node) {
            is AsExpressionModel -> lowerAsExpressionModel(NodeOwner(expression, ownerContext))
            is BinaryExpressionModel -> lowerBinaryExpressionModel(NodeOwner(expression, ownerContext))
            is CallExpressionModel -> lowerCallExpressionModel(NodeOwner(expression, ownerContext))
            is ConditionalExpressionModel -> lowerConditionalExpressionModel(NodeOwner(expression, ownerContext))
            is IdentifierExpressionModel -> lowerIdentifierExpressionModel(NodeOwner(expression, ownerContext))
            is IndexExpressionModel -> lowerIndexExpressionModel(NodeOwner(expression, ownerContext))
            is LiteralExpressionModel -> ownerContext.node
            is NonNullExpressionModel -> lowerNonNullExpressionModel(NodeOwner(expression, ownerContext))
            is PropertyAccessExpressionModel -> lowerPropertyAccessExpressionModel(NodeOwner(expression, ownerContext))
            is SuperExpressionModel -> ownerContext.node
            is TemplateExpressionModel -> lowerTemplateExpressionModel(NodeOwner(expression, ownerContext))
            is ThisExpressionModel -> ownerContext.node
            is UnaryExpressionModel -> lowerUnaryExpressionModel(NodeOwner(expression, ownerContext))
            else -> {
                logger.trace("skipping $expression")
                expression
            }
        }
    }

    fun lowerAsExpressionModel(ownerContext: NodeOwner<AsExpressionModel>) : AsExpressionModel {
        return ownerContext.node.copy(
            expression = lowerExpressionModel(NodeOwner(ownerContext.node.expression, ownerContext)),
            type = lowerTypeModel(NodeOwner(ownerContext.node.type, ownerContext))
        )
    }

    fun lowerBinaryExpressionModel(ownerContext: NodeOwner<BinaryExpressionModel>) : BinaryExpressionModel {
        return ownerContext.node.copy(
            left = lowerExpressionModel(NodeOwner(ownerContext.node.left, ownerContext)),
            right = lowerExpressionModel(NodeOwner(ownerContext.node.right, ownerContext))
        )
    }

    fun lowerCallExpressionModel(ownerContext: NodeOwner<CallExpressionModel>) : CallExpressionModel {
        return ownerContext.node.copy(
            expression = lowerExpressionModel(NodeOwner(ownerContext.node.expression, ownerContext)),
            arguments = ownerContext.node.arguments.map {
                lowerExpressionModel(NodeOwner(it, ownerContext))
            }
        )
    }

    fun lowerConditionalExpressionModel(ownerContext: NodeOwner<ConditionalExpressionModel>) : ConditionalExpressionModel {
        return ownerContext.node.copy(
            condition = lowerExpressionModel(NodeOwner(ownerContext.node.condition, ownerContext)),
            whenTrue = lowerExpressionModel(NodeOwner(ownerContext.node.whenTrue, ownerContext)),
            whenFalse = lowerExpressionModel(NodeOwner(ownerContext.node.whenFalse, ownerContext))
        )
    }

    fun lowerIdentifierExpressionModel(ownerContext: NodeOwner<IdentifierExpressionModel>) : IdentifierExpressionModel {
        return ownerContext.node
    }

    fun lowerIndexExpressionModel(ownerContext: NodeOwner<IndexExpressionModel>) : IndexExpressionModel {
        return ownerContext.node.copy(
            array = lowerExpressionModel(NodeOwner(ownerContext.node.array, ownerContext)),
            index = lowerExpressionModel(NodeOwner(ownerContext.node.index, ownerContext))
        )
    }

    fun lowerNonNullExpressionModel(ownerContext: NodeOwner<NonNullExpressionModel>) : NonNullExpressionModel {
        return ownerContext.node.copy(
            expression = lowerExpressionModel(NodeOwner(ownerContext.node.expression, ownerContext))
        )
    }

    fun lowerPropertyAccessExpressionModel(ownerContext: NodeOwner<PropertyAccessExpressionModel>) : PropertyAccessExpressionModel {
        return ownerContext.node.copy(
            left = lowerExpressionModel(NodeOwner(ownerContext.node.left, ownerContext)),
            right = lowerExpressionModel(NodeOwner(ownerContext.node.right, ownerContext))
        )
    }

    fun lowerTemplateExpressionModel(ownerContext: NodeOwner<TemplateExpressionModel>) : TemplateExpressionModel {
        return ownerContext.node.copy(
            tokens = ownerContext.node.tokens.map {
                lowerTemplateTokenModel(NodeOwner(it, ownerContext))
            }
        )
    }

    fun lowerTemplateTokenModel(ownerContext: NodeOwner<TemplateTokenModel>) : TemplateTokenModel {
        return when (val token = ownerContext.node) {
            is StringTemplateTokenModel -> token
            is ExpressionTemplateTokenModel -> token.copy(
                expression = lowerExpressionModel(NodeOwner(token.expression, ownerContext))
            )
            else -> {
                logger.trace("skipping $token")
                token
            }
        }
    }

    fun lowerUnaryExpressionModel(ownerContext: NodeOwner<UnaryExpressionModel>) : UnaryExpressionModel {
        return ownerContext.node.copy(
            operand = lowerExpressionModel(NodeOwner(ownerContext.node.operand, ownerContext))
        )
    }

}
