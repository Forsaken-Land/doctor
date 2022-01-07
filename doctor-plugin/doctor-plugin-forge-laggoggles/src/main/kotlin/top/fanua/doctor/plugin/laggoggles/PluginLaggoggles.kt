package top.fanua.doctor.plugin.laggoggles

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.plugin.forge.FML1Plugin
import top.fanua.doctor.plugin.laggoggles.protocol.Lag
import top.fanua.doctor.plugin.laggoggles.tools.LagEntity
import top.fanua.doctor.plugin.laggoggles.tools.LagTools
import java.util.concurrent.Future

/**
 *
 * @author WarmthDawn
 * @since 2021-06-09
 */
class PluginLagGoggles : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var lagTools: LagTools
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun enabled(manager: IPluginManager) {

        if (!manager.hasPlugin(FML1Plugin::class.java)) {
            log.debug("服务器不是FML1,插件插件未加载")
            return
        }
        val forge = manager.getPlugin(FML1Plugin::class.java)
        if (forge.modList.keys.contains("laggoggles")) forge.modRegistry.registerGroup(Lag)
        else {
            log.debug("服务器没有lagGoggles,插件未加载")
            return
        }

        lagTools = LagTools(client)
    }

}

class LagException(msg: String) : RuntimeException(msg)

val MinecraftClient.lagTools: LagTools?
    get() = plugin<PluginLagGoggles>()?.lagTools

fun MinecraftClient.getLag(): Future<List<LagEntity>> {
    return this.lagTools?.getLag() ?: throw LagException("客户端未启用Lag")
}

suspend fun MinecraftClient.getLagSuspend(): List<LagEntity> {
    val lagTools = this.lagTools ?: throw LagException("客户端未启用Lag")
    return lagTools.getLagSuspend()
}
