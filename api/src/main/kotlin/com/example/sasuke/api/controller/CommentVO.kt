package com.example.sasuke.api.controller

import com.example.sasuke.integrated.db.entity.Post

data class CommentVO(
    val id: Long,
    val content: String,
    val authorId: Long,
    val post: Post,
    val depth: Int,
    val parentId: Long,
)
