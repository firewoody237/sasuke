package com.example.sasuke.integrated.db.service.impl

import com.example.sasuke.integrated.db.enum.Category
import com.example.sasuke.integrated.common.resultcode.ResultCode
import com.example.sasuke.integrated.common.resultcode.ResultCodeException
import com.example.sasuke.integrated.db.dto.CreatePostDTO
import com.example.sasuke.integrated.db.dto.DeletePostDTO
import com.example.sasuke.integrated.db.dto.GetPostDTO
import com.example.sasuke.integrated.db.dto.UpdatePostDTO
import com.example.sasuke.integrated.db.entity.Post
import com.example.sasuke.integrated.db.mapper.PostMapper
import com.example.sasuke.integrated.db.repository.PostRepository
import com.example.sasuke.integrated.db.service.PostService
import com.example.sasuke.integrated.db.service.UserApiService
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class PostServiceImpl(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val userApiService: UserApiService,
) : PostService {

    companion object {
        private val log = LogManager.getLogger()
    }

    /**
     * Category + AuthorId/Title/Content
     * 2가지 조합으로 사용
     */
    override fun getPosts(getPostDTO: GetPostDTO, pageRequest: PageRequest): List<Post> {
        log.debug("getPosts, getPostDTO = '$getPostDTO'")


        return postRepository.findAll()

        /*if (getPostDTO.authorId.isNullOrEmpty() && getPostDTO.title.isNullOrEmpty() && getPostDTO.content.isNullOrEmpty()) {
            return postRepository.findByCategoryAndDeletedAtIsNotNull(
                Category.valueOf(getPostDTO.category),
                pageRequest
            )
        }

        return when {
            //저자(+카테고리)만 존재
            getPostDTO.title.isNullOrEmpty() && getPostDTO.content.isNullOrEmpty() -> {
                postRepository.findByAuthorAndCategoryAndDeletedAtIsNotNull(
                    User(
                        id = getPostDTO.authorId!!
                    ),
                    Category.valueOf(getPostDTO.category),
                    pageRequest
                )
            }

            //제목(+카테고리)만 존재
            getPostDTO.authorId.isNullOrEmpty() && getPostDTO.content.isNullOrEmpty() -> {
                postRepository.findByTitleLikeAndCategoryAndDeletedAtIsNotNull(
                    getPostDTO.title!!,
                    Category.valueOf(getPostDTO.category),
                    pageRequest
                )
            }

            //내용(+카테고리)만 존재
            getPostDTO.authorId.isNullOrEmpty() && getPostDTO.title.isNullOrEmpty() -> {
                postRepository.findByContentLikeAndCategoryAndDeletedAtIsNotNull(
                    getPostDTO.content!!,
                    Category.valueOf(getPostDTO.category),
                    pageRequest
                )
            }

            else -> {
                throw ResultCodeException(
                    ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                    loglevel = Level.INFO
                )
            }
        }*/
    }

    override fun getPost(id: Long): Post {
        log.debug("getPost, id = '$id'")

        val postOptional = try {
            postRepository.findById(id)
        } catch (e: Exception) {
            log.error("getPost DB search failed. $id", e)
            throw ResultCodeException(ResultCode.ERROR_DB, loglevel = Level.ERROR)
        }

        if (postOptional.isPresent && postOptional.get().deletedAt == null) {
            return postOptional.get()
        } else {
            throw ResultCodeException(ResultCode.ERROR_POST_NOT_EXIST, loglevel = Level.INFO)
        }
    }

    override fun createPost(createPostDTO: CreatePostDTO): Post {
        log.debug("createPost, createPostDTO='$createPostDTO'")

        if (createPostDTO.authorName.isNullOrEmpty()) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "저자명이 없습니다."
            )
        }

        val author = userApiService.getUserByName(createPostDTO.authorName!!)

        when {
            createPostDTO.title.isNullOrEmpty() -> {
                throw ResultCodeException(
                    ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                    loglevel = Level.WARN,
                    message = "title이 없습니다."
                )
            }

            createPostDTO.content.isNullOrEmpty() -> {
                throw ResultCodeException(
                    ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                    loglevel = Level.WARN,
                    message = "content가 없습니다."
                )
            }

            createPostDTO.category.isNullOrEmpty() -> {
                throw ResultCodeException(
                    ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                    loglevel = Level.WARN,
                    message = "category가 없습니다."
                )
            }
        }

        val post = Post(
            title = createPostDTO.title!!,
            content = createPostDTO.content!!,
            category = Category.valueOf(createPostDTO.category!!),
            authorName = author.name!!
        )

        return try {
            postRepository.save(post)
        } catch (e: Exception) {
            throw ResultCodeException(
                ResultCode.ERROR_DB,
                loglevel = Level.WARN
            )
        }
    }

    override fun updatePost(updatePostDTO: UpdatePostDTO): Boolean {
        log.debug("updatePost, updatePostDTO='$updatePostDTO'")

        if (updatePostDTO.id == null || updatePostDTO.authorName.isNullOrEmpty()) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
            )
        }

        //Post Check
        val foundPost = getPost(updatePostDTO.id!!)
        //User Check
        val foundUser = userApiService.getUserByName(updatePostDTO.authorName!!)

        //Author Check
        if (foundPost.authorName != foundUser.name) {
            throw ResultCodeException(ResultCode.ERROR_REQUESTER_NOT_POST_AUTHOR, loglevel = Level.WARN)
        }

        var isChange = false

        if (updatePostDTO.title?.isNotEmpty() == true) {
            foundPost.title = updatePostDTO.title!!
            isChange = true
        }

        if (updatePostDTO.content?.isNotEmpty() == true) {
            foundPost.content = updatePostDTO.content!!
            isChange = true
        }

        if (updatePostDTO.category?.isNotEmpty() == true) {
            val category = Category.valueOf(updatePostDTO.category!!)
            foundPost.category = category
            isChange = true
        }


        return try {
            when (isChange) {
                true -> {
                    postRepository.save(foundPost)
                    true
                }

                else -> {
                    false
                }
            }
        } catch (e: Exception) {
            throw ResultCodeException(ResultCode.ERROR_DB, loglevel = Level.ERROR)
        }
    }

    override fun deletePost(deletePostDTO: DeletePostDTO): Boolean {
        log.debug("deletePost deletePostDTO='$deletePostDTO'")

        if (deletePostDTO.id == null || deletePostDTO.authorName == null) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS, loglevel = Level.WARN,
                "게시글ID나 사용자ID를 모두 입력 해 주세요"
            )
        }

        val foundUser = userApiService.getUserByName(deletePostDTO.authorName!!)
        val foundPost = getPost(deletePostDTO.id!!)

        if (foundPost.authorName != foundUser.name) {
            throw ResultCodeException(ResultCode.ERROR_REQUESTER_NOT_POST_AUTHOR, loglevel = Level.WARN)
        }

        return try {
            foundPost.deletedAt = LocalDateTime.now()
            postRepository.save(foundPost)
            true
        } catch (e: Exception) {
            throw ResultCodeException(ResultCode.ERROR_DB, loglevel = Level.ERROR)
        }
    }
}