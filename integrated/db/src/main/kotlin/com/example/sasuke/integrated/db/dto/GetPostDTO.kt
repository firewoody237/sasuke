package com.example.sasuke.integrated.db.dto

import com.example.sasuke.integrated.db.enum.Category
import com.example.sasuke.integrated.common.constant.PAGE
import com.example.sasuke.integrated.common.constant.SIZE


data class GetPostDTO(
    val category: String = Category.UNCATEGORIZED.toString(),
    val authorId: String?,
    val title: String?,
    val content: String?,

    val page: Int = PAGE,
    val size: Int = SIZE
)