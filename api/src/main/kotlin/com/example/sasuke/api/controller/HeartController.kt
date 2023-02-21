package com.example.sasuke.api.controller

import com.example.sasuke.integrated.db.dto.ToggleHeartDTO
import com.example.sasuke.integrated.db.model.User
import com.example.sasuke.integrated.db.service.HeartService
import com.example.sasuke.integrated.webservice.api.ApiRequestMapping
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class HeartController(
    private val heartService: HeartService
) {
    companion object {
        private val log = LogManager.getLogger()
    }

    @ApiRequestMapping("/heart/heart", method = [RequestMethod.POST])
    fun heart(@RequestBody toggleHeartDTO: ToggleHeartDTO) {
        log.debug("heart, toggleHeartDTO = '$toggleHeartDTO'")

        heartService.heart(toggleHeartDTO)
    }

    @ApiRequestMapping("/heart/unheart", method = [RequestMethod.POST])
    fun unheart(@RequestBody toggleHeartDTO: ToggleHeartDTO) {
        log.debug("heart, toggleHeartDTO = '$toggleHeartDTO'")

        heartService.unheart(toggleHeartDTO)
    }

    @ApiRequestMapping("/heart/posts/{userId}", method = [RequestMethod.GET])
    fun getHeartedPosts(@PathVariable userId: Long): MutableList<PostVO> {
        log.debug("getHeartedPosts, userId = '$userId'")

        return heartService.getHeartedPosts(userId).map {
            post -> PostVO(
                id = post.id,
                authorId = post.authorId,
                category = post.category,
                title = post.title,
                content = post.content
            )
        }.toMutableList()
    }

    @ApiRequestMapping("/heart/users/{postId}", method = [RequestMethod.GET])
    fun getHeartedUsers(@PathVariable postId: Long): MutableList<User> {
        log.debug("getHeartedPosts, postId = '$postId'")

        return heartService.getHeartedUsers(postId)
    }
}