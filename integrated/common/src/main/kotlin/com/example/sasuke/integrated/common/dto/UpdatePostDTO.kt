package com.example.sasuke.integrated.common.dto

data class UpdatePostDTO(
    val id: Long?,
    val authorName: String?,
    var title: String?,
    var content: String?,
    var category: String?
)
