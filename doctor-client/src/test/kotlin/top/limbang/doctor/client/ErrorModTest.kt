package top.limbang.doctor.client

import top.limbang.doctor.client.entity.ForgeFeature
import top.limbang.doctor.client.entity.ForgeInfo
import top.limbang.doctor.client.entity.ServerInfo
import top.limbang.doctor.client.factory.NetworkManagerFactory
import top.limbang.doctor.client.handler.PacketForwardingHandler
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.listener.PlayListener
import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin

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
            host, port, pluginManager, serverInfo.versionName, this
        )

        networkManager
            .addListener(loginListener)
            .addListener(PlayListener())
            .addListener(PacketForwardingHandler())

        networkManager.connect()
    }
}

fun main(){
    val client = ErrorModTest()
    client.start()

}