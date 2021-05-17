package top.limbang.doctor.client.yggdrasil

import kotlinx.serialization.Serializable
import top.limbang.doctor.client.session.GameProfile

/**
 * ### 登录请求实体
 */
@Serializable
data class AuthenticateRequest(
    val username: String,
    val password: String,
    val agent: Agent = Agent()
) {
    @Serializable
    data class Agent(
        val name: String = "Minecraft",
        val version: Int = 1
    )
}


@Serializable
data class RefreshRequest(
    val accessToken: String,
    val clientToken: String?,
    val requestUser: Boolean,
    val selectedProfile: GameProfile?
)

@Serializable
data class ValidateRequest(
    val accessToken: String,
    val clientToken: String?
)
