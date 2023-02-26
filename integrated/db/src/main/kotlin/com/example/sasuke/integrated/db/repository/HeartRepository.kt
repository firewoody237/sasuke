package com.example.sasuke.integrated.db.repository

import com.example.sasuke.integrated.db.entity.Heart
import com.example.sasuke.integrated.db.entity.Post
import com.example.sasuke.integrated.db.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface HeartRepository: JpaRepository<Heart, Long?> {
    fun getHeartsByPost(post: Post): MutableList<Heart>
    fun getHeartsByUserId(userId: Long): MutableList<Heart>
    fun existsByUserIdAndPost(userId: Long, post: Post): Boolean
    fun findByUserIdAndPost(userId: Long, post: Post): Optional<Heart>
}