package top.fanua.doctor.plugin.ftbquests

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.core.api.plugin.Plugin
import top.fanua.doctor.plugin.forge.FML1Plugin
import top.fanua.doctor.plugin.ftbquests.protocol.FtbQuests

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:13:39
 */
class PluginFtbQuests : ClientPlugin {
    override lateinit var client: MinecraftClient
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override val dependencies: List<Class<out Plugin>> = listOf(FML1Plugin::class.java)

    override fun enabled(manager: IPluginManager) {
        if (!manager.hasPlugin(FML1Plugin::class.java)) {
            log.debug("服务器不是FML1,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML1Plugin::class.java)
        if (forge.modList.keys.contains("ftbquests")) forge.modRegistry.registerGroup(FtbQuests)
        else {
            log.debug("服务器没有ftbquests,插件未加载")
            return
        }

    }


}
