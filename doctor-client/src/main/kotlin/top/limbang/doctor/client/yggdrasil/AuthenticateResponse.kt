package top.limbang.doctor.client.yggdrasil

import kotlinx.serialization.Serializable
import top.limbang.doctor.client.session.GameProfile

/**
 * ### 登录响应实体
 */
@Serializable
data class AuthenticateResponse(
    val accessToken: String,
    val availableProfiles: List<GameProfile>,
    val clientToken: String,
    val selectedProfile: GameProfile
)

