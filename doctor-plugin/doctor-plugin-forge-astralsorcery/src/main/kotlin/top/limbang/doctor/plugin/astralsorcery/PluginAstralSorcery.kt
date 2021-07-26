package top.limbang.doctor.plugin.astralsorcery

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
    override lateinit var client: MinecraftClient
    override val dependencies: List<Class<out Plugin>> = listOf(FML2Plugin::class.java)

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML2Plugin::class.java)) {
            throw Exception("必须先注册ForgePlugin")
        }
        val forge = manager.getPlugin(FML2Plugin::class.java)
        if (forge.modList.keys.contains("astralsorcery")) forge.channelPacketRegistry.registerGroup(AstralSorcery)
        else throw Exception("服务器没有astralsorcery")
        forge.emitter.addListener(AstralSorceryListener())
    }

}
