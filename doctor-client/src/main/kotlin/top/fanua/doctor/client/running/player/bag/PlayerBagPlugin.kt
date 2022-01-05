package top.fanua.doctor.client.running.player.bag

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/5 14:16
 */
class PlayerBagPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var playerBagUtils: PlayerBagUtils
    override fun enabled(manager: IPluginManager) {
        this.playerBagUtils = PlayerBagUtils(client)
    }

}

val MinecraftClient.getPlayerBagUtils: PlayerBagUtils
    get() = plugin<PlayerBagPlugin>()?.playerBagUtils ?: throw RuntimeException("未开启玩家背包功能")
