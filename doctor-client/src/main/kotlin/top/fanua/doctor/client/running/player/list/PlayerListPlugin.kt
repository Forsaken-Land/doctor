package top.fanua.doctor.client.running.player.list

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager

/**
 * ### 获取玩家列表插件
 *
 * @author WarmthDawn
 * @since 2021-06-14
 */
class PlayerListPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var playerListUtils: PlayerListUtils
    override fun enabled(manager: IPluginManager) {
        this.playerListUtils = PlayerListUtils(client)
    }
}


private val MinecraftClient.playerListUtils: PlayerListUtils?
    get() = plugin<PlayerListPlugin>()?.playerListUtils

/**
 * ### 获取玩家列表
 */
fun MinecraftClient.getPlayerListTab(): PlayerListTab {
    return playerListUtils?.getPlayers() ?: throw RuntimeException("未开启玩家列表监听")
}
