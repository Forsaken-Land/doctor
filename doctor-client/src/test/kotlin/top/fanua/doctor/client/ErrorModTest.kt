package top.fanua.doctor.client

import top.fanua.doctor.client.entity.ForgeFeature
import top.fanua.doctor.client.entity.ForgeInfo
import top.fanua.doctor.client.entity.ServerInfo
import top.fanua.doctor.client.factory.NetworkManagerFactory
import top.fanua.doctor.client.listener.LoginListener
import top.fanua.doctor.client.listener.PlayListener
import top.fanua.doctor.client.session.YggdrasilMinecraftSessionService
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.impl.event.DefaultEventEmitter
import top.fanua.doctor.core.plugin.PluginManager
import top.fanua.doctor.network.core.NetworkManager
import top.fanua.doctor.plugin.forge.FML1Plugin
import top.fanua.doctor.plugin.forge.FML2Plugin

class ErrorModTest : EventEmitter by DefaultEventEmitter() {
    private lateinit var networkManager: NetworkManager
    private val pluginManager: PluginManager = PluginManager(this)

    fun start(){
        val serverInfo = ServerInfo(
            description = "test",
            versionName = "1.12.2",
            versionNumber = 340,
            forge = ForgeInfo(ForgeFeature.FML1, mutableMapOf())
        )

        if (serverInfo.forge != null) when (serverInfo.forge!!.forgeFeature) {
            ForgeFeature.FML1 -> pluginManager.registerPlugin(FML1Plugin(serverInfo.forge!!.modMap))
            ForgeFeature.FML2 -> pluginManager.registerPlugin(FML2Plugin(serverInfo.forge!!.modMap))
        }

        val suffix = if (serverInfo.forge == null) "" else serverInfo.forge!!.forgeFeature.getForgeVersion()

        val sessionService = YggdrasilMinecraftSessionService(authServerUrl, sessionServerUrl)
        val session = sessionService.loginYggdrasilWithPassword(username, password)
        val loginListener = LoginListener("", session, serverInfo.versionNumber, sessionService, suffix)

        networkManager = NetworkManagerFactory.createNetworkManager(
            host, port, pluginManager, serverInfo.versionNumber, this
        )

        networkManager
            .addListener(loginListener)
            .addListener(PlayListener())

        networkManager.connect()
    }
}

fun main(){
    val client = ErrorModTest()
    client.start()

}
