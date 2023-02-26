package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.entity.Post

data class UpdateCommentDTO(
    val id: Long?,
    val content: String?,
    val authorId: Long?,
    val post: Post?,
)
