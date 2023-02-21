package com.example.sasuke.integrated.db.service

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
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class PostService(
    private val postMapper: PostMapper,
    private val postRepository: PostRepository,
    private val userApiService: UserApiService,
) {

    companion object {
        private val log = LogManager.getLogger()
    }

    /**
     * Category + AuthorId/Title/Content
     * 2가지 조합으로 사용
     */
    fun getPosts(getPostDTO: GetPostDTO, pageRequest: PageRequest): List<Post> {
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

    fun getPostById(id: Long): Post {
        log.debug("call getPostById : id = '$id'")

        if (id == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [id]이 존재하지 않습니다."
            )
        }

        val postOptional = try {
            postRepository.findById(id)
        } catch (e: Exception) {
            log.error("getPostById DB search failed. $id", e)
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "getPostById 호출 중 DB오류 발생 : ${e.message}"
            )
        }

        return when (postOptional.isPresent && postOptional.get().deletedAt == null) {
            true -> postOptional.get()
            else -> throw ResultCodeException(
                resultCode = ResultCode.ERROR_POST_NOT_EXIST,
                loglevel = Level.WARN,
                message = "getPostById : id['$id'] 게시글이 존재하지 않습니다."
            )
        }
    }

    fun createPost(createPostDTO: CreatePostDTO): Post {
        log.debug("call createPost : createPostDTO = '$createPostDTO'")

        if (createPostDTO.authorId == 0L) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 저자ID가 존재하지 않습니다."
            )
        }

        if (createPostDTO.title.isNullOrEmpty()) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 제목이 존재하지 않습니다."
            )
        }

        if (createPostDTO.content.isNullOrEmpty()) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 내용이 존재하지 않습니다."
            )
        }

        if (createPostDTO.category.isNullOrEmpty()) {
            throw ResultCodeException(
                ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 카테고리가 존재하지 않습니다."
            )
        }

        val author = userApiService.getUserById(createPostDTO.authorId)

        return try {
            postRepository.save(
                Post(
                    title = createPostDTO.title!!,
                    content = createPostDTO.content!!,
                    category = Category.valueOf(createPostDTO.category!!),
                    authorId = author.id,
                )
            )
        } catch (e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.WARN,
                message = "createPost 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun updatePost(updatePostDTO: UpdatePostDTO): Post {
        log.debug("call updatePost : updatePostDTO = '$updatePostDTO'")

        if (updatePostDTO.id == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [ID]가 존재하지 않습니다."
            )
        }

        if (updatePostDTO.authorId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 저자ID가 존재하지 않습니다."
            )
        }

        //Post Check
        val foundPost = getPostById(updatePostDTO.id)
        //User Check
        val foundUser = userApiService.getUserById(updatePostDTO.id)

        //Author Check
        if (foundPost.authorId != foundUser.id) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_REQUESTER_NOT_POST_AUTHOR,
                loglevel = Level.WARN,
                message = "요청자와 저자가 동일하지 않습니다."
            )
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
                true -> postRepository.save(foundPost)
                else -> throw ResultCodeException(
                    resultCode = ResultCode.ERROR_NOTHING_TO_MODIFY,
                    loglevel = Level.INFO
                )
            }
        } catch (e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "updatePost 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun deletePost(deletePostDTO: DeletePostDTO) {
        log.debug("call deletePost : deletePostDTO = '$deletePostDTO'")

        if (deletePostDTO.id == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [ID]가 존재하지 않습니다."
            )
        }

        if (deletePostDTO.authorId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 저자ID가 존재하지 않습니다."
            )
        }

        val foundUser = userApiService.getUserById(deletePostDTO.authorId)
        val foundPost = getPostById(deletePostDTO.id)

        if (foundPost.authorId != foundUser.id) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_REQUESTER_NOT_POST_AUTHOR,
                loglevel = Level.WARN,
                message = "요청자와 저자가 동일하지 않습니다."
            )
        }

        try {
            foundPost.deletedAt = LocalDateTime.now()
            postRepository.save(foundPost)
        } catch (e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "deleteUpdate 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }
}