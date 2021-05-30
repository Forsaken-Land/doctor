package top.limbang.doctor.client

import io.netty.util.concurrent.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.entity.ForgeFeature
import top.limbang.doctor.client.factory.NetworkManagerFactory
import top.limbang.doctor.client.handler.PacketForwardingHandler
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.listener.PlayListener
import top.limbang.doctor.client.running.PlayerUtils
import top.limbang.doctor.client.session.YggdrasilMinecraftSessionService
import top.limbang.doctor.client.utils.ServerInfoUtils
import top.limbang.doctor.client.utils.newPromise
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.event.ConnectionEvent
import top.limbang.doctor.network.event.ConnectionEventArgs
import top.limbang.doctor.network.exception.ConnectionFailedException
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.lib.Attributes
import top.limbang.doctor.network.utils.setProtocolState
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin
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
    private var protocol: Int = 0
    private lateinit var playerUtils: PlayerUtils


    val connection get() = networkManager.connection

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

    /**
     * ### 启动客户端
     */
    fun start(host: String, port: Int): MinecraftClient {
        val pluginManager = PluginManager(this)
        val jsonStr = ping(host, port).get()
        val serviceInfo = ServerInfoUtils.getServiceInfo(jsonStr)
        protocol = serviceInfo.versionNumber

        // 注册插件
        if (serviceInfo.forge != null) when (serviceInfo.forge.forgeFeature) {
            ForgeFeature.FML1 -> pluginManager.registerPlugin(FML1Plugin(serviceInfo.forge.modMap))
            ForgeFeature.FML2 -> pluginManager.registerPlugin(FML2Plugin(serviceInfo.forge.modMap))
        }

        val suffix = if (serviceInfo.forge == null) "" else serviceInfo.forge.forgeFeature.getForgeVersion()

        // 判断是否设置了名称,有就代码离线登陆
        val loginListener: LoginListener = if (name.isEmpty()) {
            val sessionService = YggdrasilMinecraftSessionService(authServerUrl, sessionServerUrl)
            val session = sessionService.loginYggdrasilWithPassword(username, password)
            LoginListener(name, session, serviceInfo.versionNumber, sessionService, suffix)
        } else {
            LoginListener(name = name, protocolVersion = serviceInfo.versionNumber, suffix = suffix)
        }

        networkManager =
            NetworkManagerFactory.createNetworkManager(
                host, port, pluginManager, serviceInfo.versionName, this
            )

        networkManager
            .addListener(loginListener)
            .addListener(PlayListener())
            .addListener(PacketForwardingHandler())

        networkManager.connect()

        playerUtils = PlayerUtils(this)

        return this
    }

    /**
     * ### 重新连接
     */
    fun reconnect(): MinecraftClient {
        if (this::networkManager.isInitialized) {
            networkManager.connect()
        }

        return this
    }

    /**
     * ### 发送消息
     */
    fun sendMessage(msg: String) {
        networkManager.sendPacket(CChatPacket(msg))
    }

    fun getProtocol(): Int {
        return protocol
    }

    fun getPlayerUtils(): PlayerUtils {
        return playerUtils
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
                    .once(ConnectionEvent.Error) {
                        net.shutdown()
                        result.setFailure(ConnectionFailedException("连接失败"))
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
