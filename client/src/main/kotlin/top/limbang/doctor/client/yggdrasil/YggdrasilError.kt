package top.limbang.minecraft.entity.yggdrasil

import kotlinx.serialization.Serializable

/**
 * ### 错误响应
 */
@Serializable
data class YggdrasilError(
    val error: String,
    val errorMessage: String,
    val cause:String = ""
)