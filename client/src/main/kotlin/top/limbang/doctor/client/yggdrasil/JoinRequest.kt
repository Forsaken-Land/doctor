package top.limbang.doctor.client.yggdrasil

import kotlinx.serialization.Serializable
import top.limbang.doctor.client.session.UUIDSerializer
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


