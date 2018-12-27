package org.jetbrains.dukat.ast

fun Map<String, Any?>.toAst(): Declaration {
    val reflectionType = AstReflectionType.valueOf(get("reflection") as String)
    val res: Declaration
    if (reflectionType == AstReflectionType.TYPE_DECLARATION) {
        res = TypeDeclaration(if (get("value") is String) {
            get("value") as String
        } else {
            throw Exception("failed to create type declaration from ${this}")
        }, (get("params") as List<Map<String, Any?>>).map { it.toAst() as TypeDeclaration })
    } else if (reflectionType == AstReflectionType.FUNCTION_DECLARATION) {
        res = FunctionDeclaration(
            get("name") as String,
            (get("parameters") as List<Map<String, Any?>>).map { it.toAst() as ParameterDeclaration },
            (get("type") as Map<String, Any?>).toAst() as TypeDeclaration
        )
    } else if (reflectionType == AstReflectionType.PARAMETER_DECLARATION) {
        res = ParameterDeclaration(get("name") as String, (get("type") as Map<String, Any>).toAst() as TypeDeclaration)
    }  else if (reflectionType == AstReflectionType.VARIABLE_DECLARATION) {
        res = VariableDeclaration(get("name") as String, (get("type") as Map<String, Any>).toAst() as TypeDeclaration)
    } else if (reflectionType == AstReflectionType.DOCUMENT_ROOT) {
        res = DocumentRoot((get("declarations") as List<Map<String, Any?>>).map {
            it.toAst()
        })
    } else {
        println(this.get("reflection"))
        throw Exception("failed to create declaration from mapper: ${this}")
    }

    return res
}