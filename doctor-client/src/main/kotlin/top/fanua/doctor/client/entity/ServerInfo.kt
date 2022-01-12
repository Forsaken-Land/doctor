package top.fanua.doctor.client.entity

/**
 * ### 服务器信息
 *
 * - [description] 服务器描述
 * - [playerMax] 玩家最大人数
 * - [playerOnline] 玩家在线人数
 * - [playerNameList] 在线玩家名称列表
 * - [versionName] 版本名称
 * - [versionNumber] 版本号
 * - [forge] Forge 信息
 * - [modNumber] 模组数
 */
data class ServerInfo(
    val description: String = "",
    val playerMax: Int = 20,
    val playerOnline: Int = 0,
    val playerNameList: List<String> = listOf(),
    val versionName: String = "",
    val versionNumber: Int = 0,
    val forge: ForgeInfo? = null,
    val modNumber: Int = 0
)

/**
 * ### Forge 信息
 *
 * - [forgeFeature] Forge特征
 * - [modMap] modMap
 */
data class ForgeInfo(
    val forgeFeature: ForgeFeature,
    val modMap: Map<String, String>
)

/**
 * ### Forge 特征
 */
enum class ForgeFeature {
    FML1, FML2;

    /**
     * ### 获取特征
     */
    fun getFeature(): List<String> {
        return when (this) {
            FML1 -> listOf("modinfo")
            FML2 -> listOf("forgeData", "modpackData")
        }
    }

    /**
     * ### 获取 Forge 版本
     */
    fun getForgeVersion(): String {
        return when (this) {
            FML1 -> "\u0000FML\u0000"
            FML2 -> "\u0000FML2\u0000"
        }
    }
}
