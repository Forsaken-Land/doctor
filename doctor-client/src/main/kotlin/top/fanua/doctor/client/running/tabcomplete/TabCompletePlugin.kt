package top.fanua.doctor.client.running.tabcomplete

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginManager

/**
 * tab补全命令插件
 *
 * @author WarmthDawn
 * @since 2021-06-20
 */
class TabCompletePlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    lateinit var tabCompleteTool: ITabCompleteTool
    override fun enabled(manager: IPluginManager) {
        tabCompleteTool = when (client.protocolVersion) {
            340 -> {
                TabCompleteTool112(client)
            }
            5 -> {
                TabCompleteTool17(client)
            }
            else -> {
                TabCompleteTool116(client)
            }
        }
    }
}


val MinecraftClient.tabCompleteTool: ITabCompleteTool
    get() = plugin<TabCompletePlugin>()?.tabCompleteTool ?: throw RuntimeException("未开启插件")
