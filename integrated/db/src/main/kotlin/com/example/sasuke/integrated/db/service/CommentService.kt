package com.example.sasuke.integrated.db.service;

import com.example.sasuke.integrated.common.resultcode.ResultCode
import com.example.sasuke.integrated.common.resultcode.ResultCodeException
import com.example.sasuke.integrated.db.dto.CreateCommentDTO
import com.example.sasuke.integrated.db.dto.DeleteCommentDTO
import com.example.sasuke.integrated.db.dto.GetCommentsDTO
import com.example.sasuke.integrated.db.dto.UpdateCommentDTO
import com.example.sasuke.integrated.db.entity.Comment
import com.example.sasuke.integrated.db.repository.CommentRepository;
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service;
import java.time.LocalDateTime

@Service
class CommentService(
    val commentRepository:CommentRepository,
    val postService: PostService,
    val userApiService: UserApiService,
) {
    companion object {
        private val log = LogManager.getLogger()
    }

    fun getComment(id: Long): Comment {
        log.debug("call getComment : id = '$id'")

        if (id == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [id]가 존재하지 않습니다."
            )
        }

        val optionalComment = try {
            commentRepository.findById(id)
        } catch (e: Exception) {
            log.error("getComment DB search failed. $id", e)
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "getComment 호출 중 DB오류 발생 : ${e.message}"
            )
        }

        return when (optionalComment.isPresent) {
            true -> optionalComment.get()
            else -> throw ResultCodeException(
                resultCode = ResultCode.ERROR_COMMENT_NOT_EXIST,
                loglevel = Level.WARN,
                message = "getComment : id['$id'] 댓글이 존재하지 않습니다."
            )
        }
    }

    //게시글의 댓글들 검색
    fun getPostComments(getCommentsDTO: GetCommentsDTO): MutableList<Comment> {
        log.debug("call getPostComments : getCommentsDTO = '$getCommentsDTO'")

        if (getCommentsDTO.postId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [postId]가 존재하지 않습니다."
            )
        }

        return try {
            val foundPost = postService.getPostById(getCommentsDTO.postId)
            commentRepository.findAllByPostAndDeletedAtIsNotNull(foundPost)
        } catch(e:Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "getPostComments 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun createComment(createCommentDTO: CreateCommentDTO): Comment {
        log.debug("call createComment : createCommentDTO = '$createCommentDTO'")

        if (createCommentDTO.content.isNullOrEmpty()) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [content]이 존재하지 않습니다."
            )
        }

        if (createCommentDTO.authorId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [authorId]이 존재하지 않습니다."
            )
        }

        if (createCommentDTO.post == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [post]이 존재하지 않습니다."
            )
        }

        if (createCommentDTO.parentId != 0L && commentRepository.existsById(createCommentDTO.parentId)) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_COMMENT_NOT_EXIST,
                loglevel = Level.WARN,
                message = "PARENT ID에 맞는 PARENT COMMENT가 존재하지 않습니다."
            )
        }

        val foundUser = userApiService.getUserById(createCommentDTO.authorId)
        val foundPost = postService.getPostById(createCommentDTO.post.id)

        return try {
            commentRepository.save(
                Comment(
                    content = createCommentDTO.content,
                    authorId = foundUser.id,
                    post = foundPost,
                    depth = createCommentDTO.depth,
                    parentId = createCommentDTO.parentId
                )
            )
        } catch (e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.WARN,
                message = "createComment 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun updateComment(updateCommentDTO: UpdateCommentDTO): Comment {
        log.debug("call updateComment : updateCommentDTO = '$updateCommentDTO'")

        if (updateCommentDTO.content.isNullOrEmpty()) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [content]이 존재하지 않습니다."
            )
        }

        if (updateCommentDTO.authorId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [authorId]이 존재하지 않습니다."
            )
        }

        if (updateCommentDTO.post == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [post]이 존재하지 않습니다."
            )
        }

        val foundUser = userApiService.getUserById(updateCommentDTO.authorId)
        val foundPost = postService.getPostById(updateCommentDTO.post.id)
        val optionalComment = commentRepository.findById(updateCommentDTO.id)

        return when (optionalComment.isPresent) {
            true -> {
                val foundComment = optionalComment.get()
                if (foundComment.authorId != foundUser.id) {
                    throw ResultCodeException(
                        resultCode = ResultCode.ERROR_COMMENT_AUTHOR_NOT_MATCHED_WITH_USER,
                        loglevel = Level.ERROR
                    )
                }

                if (foundComment.post?.id != foundPost.id) {
                    throw ResultCodeException(
                        resultCode = ResultCode.ERROR_COMMENT_NOT_MATCHED_WITH_POST,
                        loglevel = Level.ERROR
                    )
                }

                if (foundComment.content == updateCommentDTO.content) {
                    throw ResultCodeException(
                        resultCode = ResultCode.ERROR_NOTHING_TO_MODIFY,
                        loglevel = Level.ERROR
                    )
                }

                foundComment.content = updateCommentDTO.content
                commentRepository.save(foundComment)
            }
            else -> throw ResultCodeException(
                resultCode = ResultCode.ERROR_COMMENT_NOT_EXIST,
                loglevel = Level.ERROR
            )
        }
    }

    fun deleteComment(deleteCommentDTO: DeleteCommentDTO) {
        log.debug("call deleteComment : deleteCommentDTO = '$deleteCommentDTO'")

        if (deleteCommentDTO.id == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [id]이 존재하지 않습니다."
            )
        }

        if (deleteCommentDTO.authorId == 0L) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [authorId]이 존재하지 않습니다."
            )
        }

        if (deleteCommentDTO.post == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [post]이 존재하지 않습니다."
            )
        }

        val foundUser = userApiService.getUserById(deleteCommentDTO.authorId)
        val foundPost = postService.getPostById(deleteCommentDTO.post.id)
        val optionalComment = commentRepository.findById(deleteCommentDTO.id)

        when (optionalComment.isPresent) {
            true -> {
                val foundComment = optionalComment.get()
                if (foundComment.authorId != foundUser.id) {
                    throw ResultCodeException(
                        resultCode = ResultCode.ERROR_COMMENT_AUTHOR_NOT_MATCHED_WITH_USER,
                        loglevel = Level.ERROR
                    )
                }

                if (foundComment.post?.id != foundPost.id) {
                    throw ResultCodeException(
                        resultCode = ResultCode.ERROR_COMMENT_NOT_MATCHED_WITH_POST,
                        loglevel = Level.ERROR
                    )
                }

                foundComment.deletedAt = LocalDateTime.now()
                commentRepository.save(foundComment)
            }
            else -> throw ResultCodeException(
                resultCode = ResultCode.ERROR_COMMENT_NOT_EXIST,
                loglevel = Level.ERROR
            )
        }
    }
}
