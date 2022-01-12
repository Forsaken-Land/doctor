package top.fanua.doctor.plugin.environmental

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.environmental.handler.EnvironmentalListener
import top.fanua.doctor.plugin.environmental.protocol.Environmental
import top.fanua.doctor.plugin.forge.FML2Plugin

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/24:0:17
 */
class PluginEnvironmental : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("服务器不是FML2,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        forge.channelPacketRegistry.registerGroup(Environmental)
        forge.emitter.addListener(EnvironmentalListener())


    }

}
