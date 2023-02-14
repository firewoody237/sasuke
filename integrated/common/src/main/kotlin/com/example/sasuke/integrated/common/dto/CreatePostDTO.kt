package com.example.sasuke.integrated.common.dto


import com.example.sasuke.integrated.common.Category

data class CreatePostDTO(
    var authorName: String?,
    var title: String?,
    var content: String?,
    var category: String? = Category.UNCATEGORIZED.toString()
)
