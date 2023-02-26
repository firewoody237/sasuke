package com.example.sasuke.api.controller

import com.example.sasuke.integrated.db.dto.CreateCommentDTO
import com.example.sasuke.integrated.db.dto.DeleteCommentDTO
import com.example.sasuke.integrated.db.dto.GetCommentsDTO
import com.example.sasuke.integrated.db.dto.UpdateCommentDTO
import com.example.sasuke.integrated.db.service.CommentService
import com.example.sasuke.integrated.webservice.api.ApiRequestMapping
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class CommentController(
    private val commentService: CommentService
) {
    companion object {
        private val log = LogManager.getLogger()
    }

    @ApiRequestMapping("/comment/{id}", method = [RequestMethod.GET])
    fun getComment(@PathVariable id: Long): CommentVO {
        log.debug("getComment, id = '$id'")

        val comment = commentService.getComment(id)
        return CommentVO(
            id = comment.id,
            content = comment.content,
            authorId = comment.authorId,
            post = comment.post!!,
            depth = comment.depth!!,
            parentId = comment.parentId
        )
    }

    @ApiRequestMapping("/comment", method = [RequestMethod.GET])
    fun getComments(@RequestBody getCommentsDTO: GetCommentsDTO): MutableList<CommentVO> {
        log.debug("getComments, getCommentsDTO = '$getCommentsDTO'")

        return commentService.getPostComments(getCommentsDTO).map {
            comment -> CommentVO(
                id = comment.id,
                content = comment.content,
                authorId = comment.authorId,
                post = comment.post!!,
                depth = comment.depth!!,
                parentId = comment.parentId
            )
        }.toMutableList()
    }

    @ApiRequestMapping("/comment", method = [RequestMethod.POST])
    fun createComment(@RequestBody createCommentDTO: CreateCommentDTO): CommentVO {
        log.debug("createComment, createCommentDTO = '$createCommentDTO'")

        val comment = commentService.createComment(createCommentDTO)
        return CommentVO(
            id = comment.id,
            content = comment.content,
            authorId = comment.authorId,
            post = comment.post!!,
            depth = comment.depth!!,
            parentId = comment.parentId
        )
    }

    @ApiRequestMapping("/comment", method = [RequestMethod.PUT])
    fun updateComment(@RequestBody updateCommentDTO: UpdateCommentDTO): CommentVO {
        log.debug("updateComment, updateCommentDTO = '$updateCommentDTO'")

        val comment = commentService.updateComment(updateCommentDTO)
        return CommentVO(
            id = comment.id,
            content = comment.content,
            authorId = comment.authorId,
            post = comment.post!!,
            depth = comment.depth!!,
            parentId = comment.parentId
        )
    }

    @ApiRequestMapping("/comment", method = [RequestMethod.DELETE])
    fun deleteComment(@RequestBody deleteCommentDTO: DeleteCommentDTO) {
        log.debug("deleteComment, deleteCommentDTO = '$deleteCommentDTO'")

        commentService.deleteComment(deleteCommentDTO)
    }
}