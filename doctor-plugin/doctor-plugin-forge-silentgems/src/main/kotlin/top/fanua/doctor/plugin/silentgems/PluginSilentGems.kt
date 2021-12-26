package top.fanua.doctor.plugin.silentgems

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.forge.FML2Plugin
import top.fanua.doctor.plugin.silentgems.handler.SilentGemsListener
import top.fanua.doctor.plugin.silentgems.protocol.SilentGems

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 20:55
 */
class PluginSilentGems : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient
    override fun enabled(manager: IPluginManager) {
        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("服务器不是FML2,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("silentgems") || forge.modList.size >= 150) {
            forge.channelPacketRegistry.registerGroup(SilentGems)
            forge.emitter.addListener(SilentGemsListener())
        } else {
            log.debug("服务器没有SilentGems,插件未加载")
            return
        }

    }
}