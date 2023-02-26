package com.example.sasuke.integrated.db.dto

data class UpdatePostDTO(
    val id: Long?,
    val authorId: Long?,
    var title: String?,
    var content: String?,
    var category: String?
)
