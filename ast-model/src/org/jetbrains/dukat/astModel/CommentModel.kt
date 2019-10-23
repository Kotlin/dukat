package org.jetbrains.dukat.astModel

import org.jetbrains.dukat.astCommon.CommentEntity

data class SimpleCommentModel(
    val text: String
) : CommentEntity

data class DocumentationCommentModel(
    val text: String
) : CommentEntity