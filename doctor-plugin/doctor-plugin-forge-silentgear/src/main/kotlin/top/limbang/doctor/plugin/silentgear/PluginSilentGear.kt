package top.limbang.doctor.plugin.silentgear

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.plugin.forge.FML2Plugin
import top.limbang.doctor.plugin.silentgear.handler.SilentGearListener
import top.limbang.doctor.plugin.silentgear.protocol.SilentGear

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:13
 */
class PluginSilentGear : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("服务器不是FML2,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("silentgear")) {
            forge.channelPacketRegistry.registerGroup(SilentGear)
            forge.emitter.addListener(SilentGearListener())
        } else {
            log.debug("服务器没有SilentGear,插件未加载")
            return
        }

    }

}
