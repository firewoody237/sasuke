package com.example.sasuke.integrated.db.dto

data class GetCommentsDTO(
    val postId: Long?
) {
    constructor(): this(0L)
}