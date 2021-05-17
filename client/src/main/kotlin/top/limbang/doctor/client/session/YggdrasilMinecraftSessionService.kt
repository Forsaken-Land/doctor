package top.limbang.doctor.client.session

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.yggdrasil.*
import top.limbang.doctor.network.core.HttpClient
import top.limbang.minecraft.entity.yggdrasil.YggdrasilError

/**
 *
 * @author WarmthDawn
 * @since 2021-05-17
 */
class Session(
    val profile: GameProfile,
    val accessToken: String,
    val clientToken: String,
)

fun AuthenticateResponse.toSession(): Session {
    return Session(
        this.selectedProfile,
        this.accessToken,
        this.clientToken
    )
}


open class YggdrasilMinecraftSessionService(
    val authServer: String = "https://authserver.mojang.com/authenticate",
    val sessionServer: String = "https://sessionserver.mojang.com"
) {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    companion object Default : YggdrasilMinecraftSessionService()

    private val json = Json { ignoreUnknownKeys = true }

    fun joinServer(session: Session, serverHash: String) {
        joinServer(session.profile, session.accessToken, serverHash)
    }

    fun joinServer(profile: GameProfile, accessToken: String, serverHash: String) {
        val joinRequest =
            JoinRequest(accessToken, profile.id!!, serverHash)
        val sessionServer = "$sessionServer/session/minecraft/join"
        val response = HttpClient.postJson(sessionServer, json.encodeToString(joinRequest))
        if (response.code != 204) {
            logger.error("进入验证出错...")
        }
    }

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