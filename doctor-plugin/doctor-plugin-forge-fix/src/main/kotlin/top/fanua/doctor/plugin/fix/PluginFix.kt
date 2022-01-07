package top.fanua.doctor.plugin.fix

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.fix.handler.TombManyGravesFix
import top.fanua.doctor.plugin.forge.FML1Plugin

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/7 9:22
 */
class PluginFix : ClientPlugin {
    override lateinit var client: MinecraftClient
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun enabled(manager: IPluginManager) {
        if (!manager.hasPlugin(FML1Plugin::class.java)) {
            log.debug("服务器不是FML1,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML1Plugin::class.java)
        val mods = forge.modList.keys
        if (mods.contains("tombmanygraves")) TombManyGravesFix(client)

    }
}