package com.example.sasuke.integrated.db.dto


import com.example.sasuke.integrated.db.enum.Category

data class CreatePostDTO(
    var authorId: Long?,
    var title: String?,
    var content: String?,
    var category: String? = Category.UNCATEGORIZED.toString()
)
