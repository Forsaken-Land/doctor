package top.limbang.doctor.plugin.astralsorcery

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.plugin.astralsorcery.handler.AstralSorceryListener
import top.limbang.doctor.plugin.astralsorcery.protocol.AstralSorcery
import top.limbang.doctor.plugin.forge.FML2Plugin

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:8:26
 */
class PluginAstralSorcery : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient
    override val dependencies: List<Class<out Plugin>> = listOf(FML2Plugin::class.java)

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("必须先注册ForgePlugin")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("astralsorcery")) {
            forge.channelPacketRegistry.registerGroup(AstralSorcery)
            forge.emitter.addListener(AstralSorceryListener())
        } else {
            log.debug("服务器没有AstralSorcery")
            return
        }

    }

}
