package top.fanua.doctor.plugin.astralsorcery

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.astralsorcery.handler.AstralSorceryListener
import top.fanua.doctor.plugin.astralsorcery.protocol.AstralSorcery
import top.fanua.doctor.plugin.forge.FML2Plugin

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:8:26
 */
class PluginAstralSorcery : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("服务器不是FML2,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("astralsorcery")) {
            forge.channelPacketRegistry.registerGroup(AstralSorcery)
            forge.emitter.addListener(AstralSorceryListener())
        } else {
            log.debug("服务器没有AstralSorcery,插件未加载")
            return
        }

    }

}