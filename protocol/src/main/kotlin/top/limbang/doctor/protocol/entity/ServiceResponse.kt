package top.limbang.doctor.protocol.entity

import kotlinx.serialization.Serializable
import top.limbang.doctor.protocol.core.ProtocolException


interface SimpleServiceResponse {
    val version: ServiceResponse.Version
    fun toModMap(): Map<String, String> {
        throw ProtocolException("原版无mod列表")
    }
}

@Serializable
data class VanillaSimpleServiceResponse(
    override val version: ServiceResponse.Version
) : SimpleServiceResponse

@Serializable
data class FML1SimpleServiceResponse(
    override val version: ServiceResponse.Version,
    val modinfo: ServiceResponse.Modinfo
) : SimpleServiceResponse {
    override fun toModMap(): Map<String, String> {
        val mods = mutableMapOf<String, String>()
        for (mod in modinfo.modList) {
            mods[mod.modid] = mod.version
        }
        return mods
    }

}

@Serializable
data class FML2SimpleServiceResponse(
    override val version: ServiceResponse.Version,
    val forgeData: ServiceResponse.ForgeData
) : SimpleServiceResponse {
    override fun toModMap(): Map<String, String> {
        val mods = mutableMapOf<String, String>()
        for (mod in forgeData.mods) {
            mods[mod.modId] = mod.modmarker
        }
        return mods
    }

}


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