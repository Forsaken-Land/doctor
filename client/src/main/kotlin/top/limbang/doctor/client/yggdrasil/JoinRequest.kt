package top.limbang.minecraft.entity.yggdrasil

import kotlinx.serialization.Serializable

/**
 * ### 验证请求实体
 */
@Serializable
data class JoinRequest(
    val accessToken: String,
    val selectedProfile: String,
    val serverId: String
)


