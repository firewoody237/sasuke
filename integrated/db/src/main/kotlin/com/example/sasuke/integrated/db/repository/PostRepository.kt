package com.example.sasuke.integrated.db.repository

import com.example.sasuke.integrated.db.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface PostRepository : JpaRepository<Post, Long?> {

    //fun findByAuthorAndCategoryAndDeletedAtIsNotNull(author: User?, category: Category?): List<Post>
    //fun findByAuthorAndContent(author: User, content: String): Optional<Post>
    //fun findByCategoryAndDeletedAtIsNotNull(category: Category, pageRequest: PageRequest) : List<Post>
    //fun findByAuthorAndCategoryAndDeletedAtIsNotNull(author: User, category: Category, pageRequest: PageRequest): List<Post>
    //fun findByTitleLikeAndCategoryAndDeletedAtIsNotNull(title: String, category: Category, pageRequest: PageRequest) : List<Post>
    //fun findByContentLikeAndCategoryAndDeletedAtIsNotNull(title: String, category: Category, pageRequest: PageRequest) : List<Post>
    //fun findAllByAuthorAndDeletedAtIsNotNull(author: User)
    fun findByIdAndDeletedAtIsNull(id: Long): Optional<Post>
}