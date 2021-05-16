package top.limbang.doctor.client.yggdrasil

import kotlinx.serialization.Serializable

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

