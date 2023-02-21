package com.example.sasuke.integrated.db.dto

data class UpdatePostDTO(
    val id: Long = 0L,
    val authorId: Long = 0L,
    var title: String?,
    var content: String?,
    var category: String?
)
