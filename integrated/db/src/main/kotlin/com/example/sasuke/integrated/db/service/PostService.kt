package com.example.sasuke.integrated.db.service

import com.example.sasuke.integrated.db.dto.CreatePostDTO
import com.example.sasuke.integrated.db.dto.DeletePostDTO
import com.example.sasuke.integrated.db.dto.GetPostDTO
import com.example.sasuke.integrated.db.dto.UpdatePostDTO
import com.example.sasuke.integrated.db.entity.Post
import org.springframework.data.domain.PageRequest

interface PostService {
    fun getPosts(getPostDTO: GetPostDTO, pageRequest: PageRequest): List<Post>
    fun getPost(id: Long): Post
    fun createPost(createPostDTO: CreatePostDTO): Post
    fun updatePost(updatePostDTO: UpdatePostDTO): Boolean
    fun deletePost(deletePostDTO: DeletePostDTO): Boolean
}