package org.jetbrains.dukat.tsmodel

data class AccessorDeclaration(
		val method: FunctionDeclaration,
		val access: ACCESS
) : MemberDeclaration {
	enum class ACCESS {
		WRITE,
		READ
	}
}