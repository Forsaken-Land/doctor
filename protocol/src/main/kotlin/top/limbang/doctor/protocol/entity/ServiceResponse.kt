package top.limbang.doctor.protocol.entity

import kotlinx.serialization.Serializable


interface SimpleServiceResponse {
    val version: ServiceResponse.Version
}

@Serializable
data class FML1SimpleServiceResponse(
    override val version: ServiceResponse.Version,
    val modinfo: ServiceResponse.Modinfo
) : SimpleServiceResponse

@Serializable
data class FML2SimpleServiceResponse(
    override val version: ServiceResponse.Version,
    val forgeData: ServiceResponse.ForgeData
) : SimpleServiceResponse

@Serializable
data class ServiceResponse(
    val description: Description,
    val players: Players,
    val version: Version,
    val favicon: String = "",
    val modinfo: Modinfo,
    val forgeData: ForgeData
) {
    @Serializable
    data class ForgeData(val channels: List<Channel>, val mods: List<Mod2>, val fmlNetworkVersion: Int)

    @Serializable
    data class Channel(val res: String, val version: String, val required: Boolean)

    @Serializable
    data class Description(val text: String)

    @Serializable
    data class Players(val max: Int, val online: Int, val sample: List<Player>)

    @Serializable
    data class Player(val id: String, val name: String)

    @Serializable
    data class Version(val name: String, val protocol: Int)

    /**
     * mod 信息
     *
     * - [type] 如果 FML 代表服务端安装了 Forge,默认为无长度字符串
     */
    @Serializable
    data class Modinfo(val type: String = "", val modList: List<Mod>)

    @Serializable
    data class Mod(val modid: String, val version: String)

    @Serializable
    data class Mod2(val modId: String, val modmarker: String)
}