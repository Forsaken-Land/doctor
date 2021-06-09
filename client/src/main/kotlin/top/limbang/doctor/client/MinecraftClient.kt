package top.limbang.doctor.client

import io.netty.util.concurrent.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.entity.ForgeFeature
import top.limbang.doctor.client.factory.NetworkManagerFactory
import top.limbang.doctor.client.handler.PacketForwardingHandler
import top.limbang.doctor.client.listener.LoginListener
import top.limbang.doctor.client.listener.PlayListener
import top.limbang.doctor.client.running.PlayerTab
import top.limbang.doctor.client.running.PlayerUtils
import top.limbang.doctor.client.running.TpsEntity
import top.limbang.doctor.client.running.TpsUtils
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
import top.limbang.doctor.plugin.forge.registry.IModPacketRegistry
import top.limbang.doctor.plugin.forge.registry.ModPacketRegistryImpl
import top.limbang.doctor.plugin.laggoggles.protocol.Lag
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.client.HandshakePacket
import top.limbang.doctor.protocol.definition.play.server.CChatPacket
import top.limbang.doctor.protocol.definition.status.client.RequestPacket
import top.limbang.doctor.protocol.definition.status.server.ResponsePacket
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
    private lateinit var tpsUtils: TpsUtils
    private var forgeFeature: ForgeFeature? = null
    private lateinit var pluginManager: PluginManager
    private var modPacketRegistry: IModPacketRegistry = ModPacketRegistryImpl()


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
     * ### 设置开启监听玩家列表
     */
    fun enablePlayerList(): MinecraftClient {
        this.playerUtils = PlayerUtils(this)
        return this
    }

    /**
     * ### 开启Lag mod功能
     */
    fun enableLag(): MinecraftClient {
        this.modPacketRegistry.registerGroup(Lag)
        return this
    }

    /**
     * ### 启动客户端
     * 默认等待时间2秒
     *
     * - [host] 服务器地址
     * - [port] 服务器端口
     */
    fun start(host: String, port: Int): Boolean {
        return start(host, port, 2000)
    }

    /**
     * ### 启动客户端
     * - [host] 服务器地址
     * - [port] 服务器端口
     * - [timeout] 等待时间 毫秒
     */
    fun start(host: String, port: Int, timeout: Long): Boolean {
        this.pluginManager = PluginManager(this)

        val jsonStr: String
        try {
            jsonStr = ping(host, port).get(timeout, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            logger.error("获取ping信息,等待超时...")
            return false
        } catch (e: ExecutionException) {
            logger.error("获取ping信息失败,${e.message}")
            return false
        }

        val serviceInfo = ServerInfoUtils.getServiceInfo(jsonStr)
        protocol = serviceInfo.versionNumber
        forgeFeature = serviceInfo.forge?.forgeFeature

        // 注册插件
        if (serviceInfo.forge != null) when (serviceInfo.forge.forgeFeature) {
            ForgeFeature.FML1 -> pluginManager.registerPlugin(FML1Plugin(serviceInfo.forge.modMap, modPacketRegistry))
            ForgeFeature.FML2 -> pluginManager.registerPlugin(FML2Plugin(serviceInfo.forge.modMap, modPacketRegistry))
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

        networkManager = NetworkManagerFactory.createNetworkManager(
            host, port, pluginManager, serviceInfo.versionName, this
        )

        networkManager
            .addListener(loginListener)
            .addListener(PlayListener())
            .addListener(PacketForwardingHandler())

        networkManager.connect()
        this.tpsUtils = TpsUtils(this)
        return true
    }

    /**
     * ### 停止客户端
     */
    fun stop() {
        if (this::networkManager.isInitialized) {
            networkManager.shutdown()
        }
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

    /**
     * ### 发送指定包
     */
    fun sendPacket(packet: Packet) {
        networkManager.sendPacket(packet)
    }

    /**
     * ### 获取版本ID
     */
    fun getProtocol(): Int {
        return protocol
    }

    /**
     * ### 获取玩家列表
     */
    fun getPlayerTab(): PlayerTab {
        if (this::playerUtils.isInitialized) {
            return playerUtils.getPlayers()
        } else {
            throw RuntimeException("未开启玩家列表监听")
        }
    }

    /**
     * ### 获取forgeTps
     */

    fun getForgeTps(): List<TpsEntity> {
        return if (forgeFeature != null && this::tpsUtils.isInitialized) {
            tpsUtils.getTps()
        } else throw RuntimeException("未开启玩家列表/或服务器不是Forge监听")
    }

    /**
     * ### 获取FML特征
     */
    fun getForgeFeature(): ForgeFeature? {
        return this.forgeFeature
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
                    .once(ConnectionEvent.Disconnect) {
                        net.shutdown()
                        result.setFailure(ConnectionFailedException("连接断开"))
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
