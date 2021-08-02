package top.fanua.doctor.client.running

import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.entity.ForgeFeature
import top.fanua.doctor.client.entity.ServerInfo
import top.fanua.doctor.client.listener.LoginListener
import top.fanua.doctor.client.plugin.ClientAddListenerHook
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.core.api.plugin.IPluginHookManager
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.core.plugin.addHandler
import top.fanua.doctor.plugin.forge.FML1Plugin
import top.fanua.doctor.plugin.forge.FML2Plugin

/**
 * ### 自动获取 Forge 版本插件
 *
 * @author WarmthDawn
 * @since 2021-06-13
 */
class AutoVersionForgePlugin : ClientPlugin {
    override lateinit var client: MinecraftClient
    var forgeFeature: ForgeFeature? = null
    lateinit var pluginManager: IPluginManager
    lateinit var hostSuffix: String
    override fun created(manager: IPluginManager) {
        this.pluginManager = manager
    }

    override fun beforeEnable(serverInfo: ServerInfo) {
        forgeFeature = serverInfo.forge?.forgeFeature

        // 注册插件
        if (serverInfo.forge != null) when (serverInfo.forge.forgeFeature) {
            ForgeFeature.FML1 -> pluginManager.registerPlugin(FML1Plugin(serverInfo.forge.modMap))
            ForgeFeature.FML2 -> pluginManager.registerPlugin(FML2Plugin(serverInfo.forge.modMap))
        }

        hostSuffix = if (serverInfo.forge == null) "" else serverInfo.forge.forgeFeature.getForgeVersion()

    }

    override fun registerHook(manager: IPluginHookManager) {
        manager.getHook(ClientAddListenerHook).addHandler(this) {
            if (it.message is LoginListener) {
                val login = it.message as LoginListener
                login.suffix = hostSuffix
                it.edited = true
            }
            true
        }
    }
}

val MinecraftClient.forgeFeature: ForgeFeature?
    get() = plugin<AutoVersionForgePlugin>()?.forgeFeature
