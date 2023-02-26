package com.example.sasuke.api.controller

import com.example.sasuke.integrated.db.enum.Category

data class PostVO(
    val id: Long,
    val authorId: Long,
    val category: Category,
    val title: String,
    val content: String,
)
