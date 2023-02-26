package com.example.sasuke.integrated.db.service

import com.example.sasuke.integrated.common.resultcode.ResultCode
import com.example.sasuke.integrated.common.resultcode.ResultCodeException
import com.example.sasuke.integrated.db.dto.ToggleHeartDTO
import com.example.sasuke.integrated.db.entity.Heart
import com.example.sasuke.integrated.db.entity.Post
import com.example.sasuke.integrated.db.model.User
import com.example.sasuke.integrated.db.repository.HeartRepository
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
class HeartService(
    private val heartRepository: HeartRepository,
    private val postService: PostService,
    private val userApiService: UserApiService,
) {
    companion object {
        private val log = LogManager.getLogger()
    }

    //사용자가 본인이 하트 누른 글을 찾아올 때
    fun getHeartedPosts(userId: Long?): MutableList<Post> {
        log.debug("call getHeartedPosts : userId = '$userId'")

        if (userId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [userId]가 존재하지 않습니다."
            )
        }

        return try {
            val foundUser = userApiService.getUserById(userId)
            heartRepository.getHeartsByUserId(foundUser.id).map {
                heart -> heart.post!!
            }.toMutableList()
        } catch(e:Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "getHeartedPosts 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    //해당 글을 하트 누른 유저를 창아올 때
    fun getHeartedUsers(postId: Long?): MutableList<User> {
        log.debug("call getHeartedUsers : postId = '$postId'")

        if (postId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [postId]가 존재하지 않습니다."
            )
        }

        return try {
            val foundPost = postService.getPostById(postId)
            heartRepository.getHeartsByPost(foundPost).map {
                heart -> User(
                    id = heart.userId!!
                )
            }.toMutableList()
        } catch(e:Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "getHeartedUsers 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun heart(toggleHeartDTO: ToggleHeartDTO) {
        log.debug("call heart : toggleHeartDTO = '$toggleHeartDTO'")

        if (toggleHeartDTO.userId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [userId]가 존재하지 않습니다."
            )
        }

        if (toggleHeartDTO.postId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [postId]가 존재하지 않습니다."
            )
        }

        val foundPost = postService.getPostById(toggleHeartDTO.postId)
        val foundUser = userApiService.getUserById(toggleHeartDTO.userId)

        //하트했는지 확인하고, 없으면 heart
        val isExistsHeartById = heartRepository.existsByUserIdAndPost(foundUser.id, foundPost)
        try {
            when (isExistsHeartById) {
                false -> heartRepository.save(
                    Heart(
                        userId = foundUser.id,
                        post = foundPost
                    )
                )

                else -> throw ResultCodeException(
                    resultCode = ResultCode.ERROR_HEART_ALREADY_EXIST,
                    loglevel = Level.INFO
                )
            }
        } catch(e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "heart 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }

    fun unheart(toggleHeartDTO: ToggleHeartDTO) {
        log.debug("call unheart : toggleHeartDTO = '$toggleHeartDTO'")

        if (toggleHeartDTO.userId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [userId]가 존재하지 않습니다."
            )
        }

        if (toggleHeartDTO.postId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_NOT_EXISTS,
                loglevel = Level.WARN,
                message = "파라미터에 [postId]가 존재하지 않습니다."
            )
        }

        val foundPost = postService.getPostById(toggleHeartDTO.postId)
        val foundUser = userApiService.getUserById(toggleHeartDTO.userId)
        //하트했는지 확인하고, 없으면 heart
        val foundOptionalHeart = heartRepository.findByUserIdAndPost(foundUser.id, foundPost)
        try {
            when (foundOptionalHeart.isPresent) {
                true -> heartRepository.deleteById(foundOptionalHeart.get().id)
                else -> throw ResultCodeException(
                    resultCode = ResultCode.ERROR_HEART_NOT_EXIST,
                    loglevel = Level.INFO
                )
            }
        } catch(e: Exception) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_DB,
                loglevel = Level.ERROR,
                message = "heart 호출 중 DB오류 발생 : ${e.message}"
            )
        }
    }
}