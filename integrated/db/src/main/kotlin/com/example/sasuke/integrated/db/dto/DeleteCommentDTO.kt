package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.entity.Post

data class DeleteCommentDTO(
    val id: Long = 0L,
    val authorId: Long = 0L,
    val post: Post?
)
