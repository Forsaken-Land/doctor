package top.fanua.doctor.client.running

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.forge.FML1Plugin
import top.fanua.doctor.plugin.forge.FML2Plugin

/**
 * ### TPS插件
 * 基于 `/forge tps` 指令
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
class TpsPlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var tpsTools: ITpsTools
    override fun enabled(manager: IPluginManager) {
        when {
            manager.hasPlugin(AutoVersionForgePlugin::class.java) -> {
                tpsTools = TpsTools.create(client)
            }
            manager.hasPlugin(FML1Plugin::class.java) -> {
                tpsTools = TpsToolsFML1(client)
            }
            manager.hasPlugin(FML2Plugin::class.java) -> {
                tpsTools = TpsToolsFML2(client)
            }
        }
    }
}


val MinecraftClient.tpsTools: ITpsTools
    get() = plugin<TpsPlugin>()?.tpsTools ?: DummyTpsTools
