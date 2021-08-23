package top.fanua.doctor.allLoginPlugin

import top.fanua.doctor.client.MinecraftClientBuilder
import top.fanua.doctor.plugin.astralsorcery.PluginAstralSorcery
import top.fanua.doctor.plugin.environmental.PluginEnvironmental
import top.fanua.doctor.plugin.exNihiloSequentia.PluginExNihiloSequentia
import top.fanua.doctor.plugin.extendedcrafting.PluginExtendedCrafting
import top.fanua.doctor.plugin.silentgear.PluginSilentGear

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:4:03
 */
fun MinecraftClientBuilder.enableAllLoginPlugin(): MinecraftClientBuilder {
    this.plugin(PluginAstralSorcery())
        .plugin(PluginSilentGear())
        .plugin(PluginExtendedCrafting())
        .plugin(PluginExNihiloSequentia())
        .plugin(PluginEnvironmental())
    return this
}
