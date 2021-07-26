package top.limbang.doctor.plugin.silentgear

import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.plugin.ClientPlugin
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin
import top.limbang.doctor.plugin.silentgear.handler.SilentGearListener
import top.limbang.doctor.plugin.silentgear.protocol.SilentGear

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:13
 */
class PluginSilentGear : ClientPlugin {
    override lateinit var client: MinecraftClient
    override val dependencies: List<Class<out Plugin>> = listOf(FML2Plugin::class.java)

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            throw Exception("必须先注册ForgePlugin")
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("silentgear")) forge.channelPacketRegistry.registerGroup(SilentGear)
        else throw Exception("silentGear")
        forge.emitter.addListener(SilentGearListener())
    }

}
