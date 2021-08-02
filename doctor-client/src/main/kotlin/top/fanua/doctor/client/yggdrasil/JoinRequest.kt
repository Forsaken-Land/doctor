package top.fanua.doctor.client.yggdrasil

import kotlinx.serialization.Serializable
import top.fanua.doctor.client.session.UUIDSerializer
import java.util.*

/**
 * ### 验证请求实体
 */
@Serializable
data class JoinRequest(
    val accessToken: String,
    @Serializable(with = UUIDSerializer::class)
    val selectedProfile: UUID,
    val serverId: String
)


