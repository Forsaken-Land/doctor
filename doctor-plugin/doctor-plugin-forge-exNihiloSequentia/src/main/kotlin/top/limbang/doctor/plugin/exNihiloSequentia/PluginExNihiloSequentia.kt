package top.limbang.doctor.plugin.exNihiloSequentia

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.plugin.exNihiloSequentia.handler.ExNihiloSequentiaListener
import top.limbang.doctor.plugin.exNihiloSequentia.protocol.ExNihiloSequentia
import top.limbang.doctor.plugin.forge.FML2Plugin

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:8:26
 */
class PluginExNihiloSequentia : ClientPlugin {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override lateinit var client: MinecraftClient

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            log.debug("服务器不是FML2,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("exnihilosequentia")) {
            forge.channelPacketRegistry.registerGroup(ExNihiloSequentia)
            forge.emitter.addListener(ExNihiloSequentiaListener())
        } else {
            log.debug("服务器没有ExNihiloSequentia,插件未加载")
            return
        }

    }

}
