package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.entity.Post

data class UpdateCommentDTO(
    val id: Long = 0L,
    val content: String?,
    val authorId: Long = 0L,
    val post: Post?,
)
