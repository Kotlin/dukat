package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.Entity

interface CommentModel : Entity {
    val text: String
}

data class SimpleCommentModel(
        override val text: String
): CommentModel

data class DocumentationCommentModel(
        override val text: String
): CommentModel