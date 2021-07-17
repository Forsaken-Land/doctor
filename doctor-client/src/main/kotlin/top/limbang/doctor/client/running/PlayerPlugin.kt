package top.limbang.doctor.client.running

import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager

/**
 * ### 获取玩家列表插件
 *
 * @author WarmthDawn
 * @since 2021-06-14
 */
class PlayerPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var playerUtils: PlayerUtils
    override fun enabled(manager: IPluginManager) {
        this.playerUtils = PlayerUtils(client)
    }
}


val MinecraftClient.playerUtils: PlayerUtils?
    get() = plugin<PlayerPlugin>()?.playerUtils

/**
 * ### 获取玩家列表
 */
fun MinecraftClient.getPlayerTab(): PlayerTab {
    return playerUtils?.getPlayers() ?: throw RuntimeException("未开启玩家列表监听")
}