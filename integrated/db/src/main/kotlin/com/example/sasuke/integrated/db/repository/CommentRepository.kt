package com.example.sasuke.integrated.db.repository

import com.example.sasuke.integrated.db.entity.Comment
import com.example.sasuke.integrated.db.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository: JpaRepository<Comment, Long?> {
    fun findAllByPostAndDeletedAtIsNotNull(post: Post): MutableList<Comment>
}