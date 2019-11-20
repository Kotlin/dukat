package org.jetbrains.dukat.astModel.comments

import org.jetbrains.dukat.astCommon.CommentEntity

data class DocumentationCommentModel(
    val text: String
) : CommentEntity