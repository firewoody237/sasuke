package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.entity.Post

data class CreateCommentDTO(
    val content: String?,
    val authorId: Long = 0L,
    val post: Post?,
    val depth: Int = 0,
    val parentId: Long = 0L
)
