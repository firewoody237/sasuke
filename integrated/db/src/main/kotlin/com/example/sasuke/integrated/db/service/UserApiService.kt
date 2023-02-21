package com.example.sasuke.integrated.db.service


import com.example.sasuke.integrated.common.resultcode.ResultCode
import com.example.sasuke.integrated.common.resultcode.ResultCodeException
import com.example.sasuke.integrated.db.dto.GetUserDTO
import com.example.sasuke.integrated.db.enum.Grade
import com.example.sasuke.integrated.db.model.User
import io.netty.channel.ChannelOption
import io.netty.channel.ConnectTimeoutException
import net.sf.json.JSONObject
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.util.retry.Retry
import java.time.Duration

@Service
class UserApiService(
    @Value("\${user.api.host}")
    private val USER_API_HOST: String,
    @Value("\${user.api.proxy}")
    private val USER_API_PROXY: String,
    @Value("\${user.api.getUsers}")
    private val USER_API_GET_USERS: String,
    @Value("\${user.api.getUserByName}")
    private val USER_API_GET_USER_BY_NAME: String,
    @Value("\${user.api.getUserById}")
    private val USER_API_GET_USER_BY_ID: String,


    ) {

    companion object {
        private val log = LogManager.getLogger()
    }


    private val httpClient = HttpClient
        .create()
        .baseUrl(USER_API_HOST)
        .responseTimeout(Duration.ofMillis(5000))
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)

    private val webClient = WebClient
        .builder()
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()


    fun getUserByName(name: String): User {
        if (name.isEmpty()) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_TYPE,
                loglevel = Level.INFO
            )
        }

        val uri = USER_API_PROXY + USER_API_GET_USER_BY_NAME.replace("{name}", name)
        try {
            val result = webClient
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JSONObject::class.java)
                .retryWhen(
                    Retry.max(2).filter {
                        it.cause is ConnectTimeoutException
                    }
                )
                .block() ?: throw ResultCodeException(
                resultCode = ResultCode.ERROR_USER_CONNECTION,
                loglevel = Level.INFO
            )

            log.info("get User. uri: $uri, response: $result")
            if (result["rtncd"] != 1000) {
                //실패
                throw ResultCodeException(
                    resultCode = ResultCode.ERROR_USER_RESPONSE,
                    loglevel = Level.INFO
                )
            }
            val response = result["response"] as JSONObject
            val id = response["id"] as Long
            val name = response["name"] as String
            val nickname = response["nickname"] as String
            val email = response["email"] as String
            val grade = Grade.valueOf(response["grade"] as String)
            val point = response["point"] as Int

            return User(
                id = id,
                name = name,
                nickname = nickname,
                email = email,
                grade = grade,
                point = point.toLong()
            )
        } catch (e: ResultCodeException) {
            throw e
        } catch (e: Exception) {
            log.error("get User error. uri: $uri", e)
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_USER_RESPONSE,
                loglevel = Level.INFO
            )
        }
    }

    fun getUserById(userId: Long?): User {
        if (userId == null) {
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_PARAMETER_TYPE,
                loglevel = Level.INFO
            )
        }

        val uri = USER_API_PROXY + USER_API_GET_USER_BY_ID
        try {
            val result = webClient.method(HttpMethod.GET)
                .uri(uri)
                .body(GetUserDTO(id = userId), GetUserDTO::class.java)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JSONObject::class.java)
                .retryWhen(
                    Retry.max(2).filter {
                        it.cause is ConnectTimeoutException
                    }
                )
                .block() ?: throw ResultCodeException(
                resultCode = ResultCode.ERROR_USER_CONNECTION,
                loglevel = Level.INFO
            )

            log.info("get User by Id. uri: $uri, response: $result")
            if (result["rtncd"] != 1000) {
                //실패
                throw ResultCodeException(
                    resultCode = ResultCode.ERROR_USER_RESPONSE,
                    loglevel = Level.INFO
                )
            }
            val response = result["response"] as JSONObject
            val id = response["id"] as Long
            val name = response["name"] as String
            val nickname = response["nickname"] as String
            val email = response["email"] as String
            val grade = Grade.valueOf(response["grade"] as String)
            val point = response["point"] as Int

            return User(
                id = id,
                name = name,
                nickname = nickname,
                email = email,
                grade = grade,
                point = point.toLong()
            )
        } catch (e: ResultCodeException) {
            throw e
        } catch (e: Exception) {
            log.error("get User error. uri: $uri", e)
            throw ResultCodeException(
                resultCode = ResultCode.ERROR_USER_RESPONSE,
                loglevel = Level.INFO
            )
        }
    }
}