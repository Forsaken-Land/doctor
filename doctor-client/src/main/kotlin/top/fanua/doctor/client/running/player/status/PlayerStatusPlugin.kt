package top.fanua.doctor.client.running.player.status

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/9 10:31
 */
class PlayerStatusPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var playerStatusUtils: PlayerStatusUtils
    override fun enabled(manager: IPluginManager) {
        this.playerStatusUtils = PlayerStatusUtils(client)
    }
}

private val MinecraftClient.playerStatusUtils: PlayerStatusUtils?
    get() = plugin<PlayerStatusPlugin>()?.playerStatusUtils

/**
 * ### 获取玩家状态
 */
fun MinecraftClient.getPlayerStatus(): PlayerStatus {
    return playerStatusUtils?.getStatus() ?: throw RuntimeException("未开启玩家列表监听")
}