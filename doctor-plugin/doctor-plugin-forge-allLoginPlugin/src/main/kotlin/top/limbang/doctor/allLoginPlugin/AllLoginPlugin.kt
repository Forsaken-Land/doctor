package top.limbang.doctor.allLoginPlugin

import top.limbang.doctor.client.MinecraftClientBuilder
import top.limbang.doctor.plugin.astralsorcery.PluginAstralSorcery
import top.limbang.doctor.plugin.extendedcrafting.PluginExtendedCrafting
import top.limbang.doctor.plugin.silentgear.PluginSilentGear

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:4:03
 */
fun MinecraftClientBuilder.enableAllLoginPlugin(): MinecraftClientBuilder {
    this.plugin(PluginAstralSorcery())
        .plugin(PluginSilentGear())
        .plugin(PluginExtendedCrafting())
    return this
}
