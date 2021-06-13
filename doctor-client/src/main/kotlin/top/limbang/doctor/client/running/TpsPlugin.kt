package top.limbang.doctor.client.running

import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin

/**
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
