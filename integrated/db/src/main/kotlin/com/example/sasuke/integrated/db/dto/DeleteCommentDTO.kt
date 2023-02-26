package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.entity.Post

data class DeleteCommentDTO(
    val id: Long?,
    val authorId: Long?,
    val post: Post?
)
