package top.fanua.doctor.client.running.player.world

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/12:13:39
 */
class PlayerWorldPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var playerWorldUtils: PlayerWorldUtils
    override fun enabled(manager: IPluginManager) {
        this.playerWorldUtils = PlayerWorldUtils(client)
    }


}

private val MinecraftClient.playerWorldUtils: PlayerWorldUtils?
    get() = plugin<PlayerWorldPlugin>()?.playerWorldUtils

fun MinecraftClient.getWorld() = playerWorldUtils?.world ?: throw RuntimeException("未开启世界读取")
