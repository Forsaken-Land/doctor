package top.limbang.doctor.client.session

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.yggdrasil.*
import top.limbang.doctor.network.core.HttpClient

/**
 * ### 会话
 */
class Session(
    val profile: GameProfile,
    val accessToken: String,
    val clientToken: String,
)

/**
 * ### 提取会话
 */
fun AuthenticateResponse.toSession(): Session {
    return Session(
        this.selectedProfile,
        this.accessToken,
        this.clientToken
    )
}

/**
 * ### Yggdrasil SessionService
 * @param authServer 验证服务器Url
 * @param sessionServer 会话服务器Url
 */
open class YggdrasilMinecraftSessionService(
    private val authServer: String = "https://authserver.mojang.com/authenticate",
    private val sessionServer: String = "https://sessionserver.mojang.com"
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    companion object Default : YggdrasilMinecraftSessionService()

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * ### 进入服务器
     * @param session 会话
     * @param serverHash 服务器哈希
     */
    fun joinServer(session: Session, serverHash: String) {
        joinServer(session.profile, session.accessToken, serverHash)
    }

    /**
     * ### 进入服务器
     * @param profile
     * @param accessToken 访问令牌
     * @param serverHash 服务器哈希
     */
    fun joinServer(profile: GameProfile, accessToken: String, serverHash: String) {
        val joinRequest =
            JoinRequest(accessToken, profile.id!!, serverHash)
        val sessionServer = "$sessionServer/session/minecraft/join"
        val response = HttpClient.postJson(sessionServer, json.encodeToString(joinRequest))
        if (response.code != 204) {
            logger.error("进入验证出错...")
        }
    }

    /**
     * ### 登陆服务器
     * @param username 用户名
     * @param password 密码
     * @return [Session]
     */
    fun loginYggdrasilWithPassword(username: String, password: String): Session {
        val authServer = "$authServer/authenticate"
        val body = json.encodeToString(AuthenticateRequest(username, password))
        val response = HttpClient.postJson(authServer, body)
        if (response.code != 200) {
            val errorResponse = json.decodeFromString<YggdrasilError>(response.content)
            throw AuthenticationException("登录验证出错...: ${errorResponse.errorMessage}")
        }
        return json.decodeFromString<AuthenticateResponse>(response.content).toSession()

    }

    /**
     * ### 验证 yggdrasil 会话
     * @param session 会话
     */
    fun validateYggdrasilSession(session: Session): Session {
        val client = HttpClient()
        if (client.postJson(
                "$authServer/validate",
                json.encodeToString(ValidateRequest(session.accessToken, session.clientToken))
            ).code != 200
        ) {
            val body = json.encodeToString(RefreshRequest(session.accessToken, session.clientToken, true, null))
            val response = HttpClient.postJson("$authServer/refresh", body)
            if (response.code != 200) {
                val errorResponse = json.decodeFromString<YggdrasilError>(response.content)
                throw AuthenticationException("登录验证出错...: ${errorResponse.errorMessage}")
            }
            return json.decodeFromString<AuthenticateResponse>(response.content).toSession()

        }
        return session
    }


}