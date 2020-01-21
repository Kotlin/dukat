package org.jetbrains.dukat.tsmodel.importClause

data class ImportDeclaration(val clause: ImportClauseDeclaration, val referenedFile: String)

data class ImportSpecifier(val name: String, val propertyName: String?)

sealed class ImportClauseDeclaration
data class NamespaceImportDeclaration(val name: String) : ImportClauseDeclaration()
data class NamedImportsDeclaration(val importSpecifiers: List<ImportSpecifier>): ImportClauseDeclaration()