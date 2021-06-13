package top.limbang.doctor.plugin.laggoggles

import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.laggoggles.protocol.Lag

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
object PluginLagGoggles : Plugin {
    override val dependencies: List<Class<out Plugin>> = listOf(FML1Plugin::class.java)

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML1Plugin::class.java)) {
            throw Exception("必须先注册ForgePlugin")
        }
        val forge = manager.getPlugin(FML1Plugin::class.java)
        if (forge.modList.keys.contains("laggoggles")) forge.modRegistry.registerGroup(Lag)
        else throw Exception("服务器没有lagGoggles")
    }

}