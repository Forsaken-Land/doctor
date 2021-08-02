package top.fanua.doctor.client

import io.netty.util.concurrent.Promise
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.entity.ServerInfo
import top.fanua.doctor.client.factory.NetworkManagerFactory
import top.fanua.doctor.client.listener.LoginListener
import top.fanua.doctor.client.listener.PlayListener
import top.fanua.doctor.client.plugin.ClientAddListenerHook
import top.fanua.doctor.client.plugin.ClientPlugin
import top.fanua.doctor.client.session.Session
import top.fanua.doctor.client.session.YggdrasilMinecraftSessionService
import top.fanua.doctor.client.utils.ServerInfoUtils
import top.fanua.doctor.client.utils.newPromise
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.event.EventListener
import top.fanua.doctor.core.api.plugin.Plugin
import top.fanua.doctor.core.api.plugin.PluginEvent
import top.fanua.doctor.core.api.plugin.invokeMutableHook
import top.fanua.doctor.core.impl.event.DefaultEventEmitter
import top.fanua.doctor.core.plugin.PluginManager
import top.fanua.doctor.network.core.NetworkManager
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.event.ConnectionEventArgs
import top.fanua.doctor.network.exception.ConnectionFailedException
import top.fanua.doctor.network.handler.PacketEvent
import top.fanua.doctor.network.lib.Attributes
import top.fanua.doctor.network.utils.setProtocolState
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.definition.client.HandshakePacket
import top.fanua.doctor.protocol.definition.play.server.CChatPacket
import top.fanua.doctor.protocol.definition.status.client.RequestPacket
import top.fanua.doctor.protocol.definition.status.server.ResponsePacket
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * ### Minecraft 客户端
 */
class MinecraftClient(
    val session: Session? = null,
    val name: String = "",
    val sessionService: YggdrasilMinecraftSessionService = YggdrasilMinecraftSessionService
) : EventEmitter by DefaultEventEmitter() {
    private lateinit var networkManager: NetworkManager
    var versionlName: String = ""
        private set
    var protocolVersion: Int = 0
        private set
    var started: Boolean = false
        private set
    val pluginManager: PluginManager = PluginManager(this)

    init {
        this.on(PluginEvent.BeforeCreate) {
            if (it.plugin is ClientPlugin) {
                (it.plugin as ClientPlugin).client = this
            }
        }
    }


    val connection get() = networkManager.connection


    inline fun <reified T : Plugin> plugin(): T? {
        return if (pluginManager.hasPlugin(T::class.java)) {
            pluginManager.getPlugin(T::class.java)
        } else {
            null
        }
    }


    fun addPlugin(plugin: Plugin): MinecraftClient {
        pluginManager.registerPlugin(plugin)
        return this
    }

    fun <T : Plugin> removePlugin(plugin: Class<T>) {
        pluginManager.removePlugin(plugin)
    }


    /**
     * ### 启动客户端
     * 默认等待时间5秒
     *
     * - [host] 服务器地址
     * - [port] 服务器端口
     */
    fun start(host: String, port: Int): Boolean {
        return start(host, port, 5000)
    }

    /**
     * ### 启动客户端
     * - [host] 服务器地址
     * - [port] 服务器端口
     * - [timeout] 等待时间 毫秒
     */
    fun start(host: String, port: Int, timeout: Long): Boolean {
        if (started) {
            throw Exception("客户端已经启动")
        }


        val serverInfo = ping(host, port, timeout, TimeUnit.MILLISECONDS) ?: return false
        versionlName = serverInfo.versionName
        protocolVersion = serverInfo.versionNumber
        // 判断是否设置了名称,有就代码离线登陆
        val loginListener: LoginListener = if (name.isEmpty()) {
            LoginListener(name, session, serverInfo.versionNumber, sessionService)
        } else {
            LoginListener(name = name, protocolVersion = serverInfo.versionNumber)
        }

        pluginManager.getAllPlugins().forEach {
            if (it is ClientPlugin) it.beforeEnable(serverInfo)
        }
        pluginManager.onPluginEnabled()

        networkManager = NetworkManagerFactory.createNetworkManager(
            host, port, pluginManager, serverInfo.versionNumber, this
        )

        networkManager
            .addListenerHooked(loginListener)
            .addListenerHooked(PlayListener())



        networkManager.connect()
        started = true
        return true
    }

    private fun EventEmitter.addListenerHooked(listener: EventListener): EventEmitter {
        val hooked = pluginManager.invokeMutableHook(ClientAddListenerHook, listener)
        return this.addListener(hooked)
    }

    /**
     * ### 停止客户端
     */
    fun stop() {
        if (started) {
            networkManager.shutdown().addListener {
                started = false
            }
        }
    }


    /**
     * ### 重新连接
     */
    fun reconnect(): MinecraftClient {
        if (started) {
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


    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MinecraftClient::class.java)

        fun builder() = MinecraftClientBuilder()

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
                    }

                net.connect()
            }
        }

        fun ping(host: String, port: Int, timeout: Long = 2000, unit: TimeUnit = TimeUnit.MILLISECONDS): ServerInfo? {
            val jsonStr: String
            try {
                jsonStr = ping(host, port).get(timeout, TimeUnit.MILLISECONDS)
            } catch (e: TimeoutException) {
                logger.error("获取ping信息,等待超时...")
                return null
            } catch (e: ExecutionException) {
                logger.error("获取ping信息失败,${e.message}")
                return null
            }
            val serverInfo = try {
                ServerInfoUtils.getServiceInfo(jsonStr)
            } catch (e: NullPointerException) {
                logger.error("服务器正在启动，请等待...")
                return null
            }


            return serverInfo
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
