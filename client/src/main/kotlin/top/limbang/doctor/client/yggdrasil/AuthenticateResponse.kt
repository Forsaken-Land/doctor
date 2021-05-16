package top.limbang.minecraft.entity.yggdrasil

import kotlinx.serialization.Serializable

/**
 * ### 登录响应实体
 */
@Serializable
data class AuthenticateResponse(
    val accessToken: String,
    val availableProfiles: List<AvailableProfile>,
    val clientToken: String,
    val selectedProfile: SelectedProfile
){
    @Serializable
    data class AvailableProfile(val id: String, val name: String)

    @Serializable
    data class SelectedProfile(val id: String, val name: String)
}

