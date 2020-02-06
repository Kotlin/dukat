package org.jetbrains.dukat.tsmodel.importClause

data class ImportDeclaration(val clause: ImportClauseDeclaration, val referencedFile: String)

data class ImportSpecifierDeclaration(val name: String, val propertyName: String?, val uid: String)

sealed class ImportClauseDeclaration
data class NamespaceImportDeclaration(val name: String) : ImportClauseDeclaration()
data class NamedImportsDeclaration(val importSpecifiers: List<ImportSpecifierDeclaration>): ImportClauseDeclaration()