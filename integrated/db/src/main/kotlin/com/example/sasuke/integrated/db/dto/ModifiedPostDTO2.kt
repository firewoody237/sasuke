package com.example.sasuke.integrated.db.dto

data class ModifiedPostDTO2(
    val authorId: String,
    val postId: Long,
    val title: String?,
    val content: String?,
    val category: String?
)
