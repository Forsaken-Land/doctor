package top.limbang.doctor.client

import io.netty.util.concurrent.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.factory.NetworkManagerFactory
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.listener.PlayListener
import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.client.utils.AutoUtils
import top.limbang.doctor.client.utils.newPromise
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.play.server.CChatPacket
import top.limbang.doctor.protocol.definition.status.client.RequestPacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket

/**
 * ### Minecraft 客户端
 */
class MinecraftClient : EventEmitter by DefaultEventEmitter() {
    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    private var username: String = ""
    private var password: String = ""
    private var name: String = ""
    private var authServerUrl = "https://authserver.mojang.com/authenticate"
    private var sessionServerUrl = "https://sessionserver.mojang.com"
    private lateinit var networkManager: NetworkManager

    /**
     * ### 设置在线登录
     */
    fun user(username: String, password: String): MinecraftClient {
        this.username = username
        this.password = password
        return this
    }

    /**
     * ### 设置离线登录名称
     */
    fun name(name: String): MinecraftClient {
        this.name = name
        return this
    }

    /**
     * ### 设置外置登录 验证地址
     */
    fun authServerUrl(url: String): MinecraftClient {
        this.authServerUrl = url
        return this
    }

    /**
     * ### 设置外置登录 session地址
     */
    fun sessionServerUrl(url: String): MinecraftClient {
        this.sessionServerUrl = url
        return this
    }

    fun start(host: String, port: Int): MinecraftClient {
        val pluginManager = PluginManager(this)
        val jsonStr = ping(host, port).get()
        val suffix = AutoUtils.autoForgeVersion(jsonStr, pluginManager)
        val version = AutoUtils.autoVersion(jsonStr)

        val sessionService = YggdrasilMinecraftSessionService(authServerUrl, sessionServerUrl)
        val session = sessionService.loginYggdrasilWithPassword(username, password)

        networkManager =
            NetworkManagerFactory.createNetworkManager(host + suffix, port, pluginManager, version, this)

        networkManager
            .addListener(LoginListener(session, AutoUtils.getProtocolVersion(jsonStr), sessionService))
            .addListener(PlayListener())

        networkManager.connect()
        return this
    }

    fun reconnect(): MinecraftClient {
        if (this::networkManager.isInitialized) {
            networkManager.connect()
        }

        return this
    }

    fun sendMessage(msg: String) {
        networkManager.sendPacket(CChatPacket(msg))
    }

    companion object {

        /**
         * ### Ping 服务器
         *
         * ping(host,port).get() 获取服务器 Json字符串
         */
        fun ping(host: String, port: Int): Promise<String> {
            return newPromise { result ->
                val net = NetworkManagerFactory.createNetworkManager(host, port)

                net.once(ConnectionEvent.Connected, this::startPing)
                    .once(PacketEvent(ResponsePacket::class)) {
                        net.shutdown()
                        result.setSuccess(it.json)
                    }

                net.connect()
            }
        }

        /**
         * ### 开始Ping
         */
        private fun startPing(arg: ConnectionEventArgs) {
            val connection = arg.context!!.channel().attr(Attributes.ATTR_CONNECTION).get()
            handshake(arg, ProtocolState.STATUS, 0)
            connection.sendPacket(RequestPacket())
        }

        /**
         * ### 握手
         */
        private fun handshake(arg: ConnectionEventArgs, protocolState: ProtocolState, version: Int) {
            val connection = arg.context!!.channel().attr(Attributes.ATTR_CONNECTION).get()
            connection.sendPacket(
                HandshakePacket(version, connection.host, connection.port, protocolState)
            ).await()
            arg.context!!.setProtocolState(protocolState)
        }


    }
}