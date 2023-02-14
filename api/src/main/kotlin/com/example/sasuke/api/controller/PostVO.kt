package com.example.sasuke.api.controller

data class PostVO(
    val id: Long,
    val authorId: String,
    val title: String,
    val content: String,
    val comment: List<Map<String, Any?>>? = null,
    val commentCount: Long? = null,
)
