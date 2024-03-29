package top.fanua.doctor.network.core

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.util.concurrent.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.plugin.HookMessage
import top.fanua.doctor.core.api.plugin.IPluginManager
import top.fanua.doctor.core.impl.event.DefaultEventEmitter
import top.fanua.doctor.core.impl.plugin.DummyPluginManager
import top.fanua.doctor.network.api.CodecInitializer
import top.fanua.doctor.network.api.Connection
import top.fanua.doctor.network.api.DummyCodecInitializer
import top.fanua.doctor.network.core.connection.ClientHandler
import top.fanua.doctor.network.core.connection.NetworkConnection
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.event.ConnectionEventArgs
import top.fanua.doctor.network.event.NetLifeCycleEvent
import top.fanua.doctor.network.handler.ReadPacketListener
import top.fanua.doctor.network.hooks.InitChannelPipelineHook
import top.fanua.doctor.network.lib.Attributes
import top.fanua.doctor.network.utils.FutureUtils
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.registry.IChannelPacketRegistry
import top.fanua.doctor.protocol.registry.IPacketRegistry
import top.fanua.doctor.protocol.version.ProtocolVersion
import top.fanua.doctor.protocol.version.createChannel
import top.fanua.doctor.protocol.version.createProtocol


/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class NetworkManager(
    private val event: EventEmitter,
    val host: String,
    val port: Int,
    val protocol: IPacketRegistry,
    val channelRegistry: IChannelPacketRegistry,
    val pluginManager: IPluginManager,
) : EventEmitter by event {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(NetworkManager::class.java)
        val MANAGER_CHANNEL = "clientHandler"
        val TIMEOUT_HANDLER = "timeout"
    }

    init {
        //包事件分发
        this.addListener(ReadPacketListener())
    }


    private val workGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private lateinit var channel: Channel

    fun connect(): Future<*> {
        this.emit(NetLifeCycleEvent.BeforeConnect, this)
        if (this::channel.isInitialized && channel.isActive)
            return FutureUtils.pass()
        return bootstrap.connect(host, port)
            .addListener {
                if (it.isSuccess) {
                    channel = (it as ChannelFuture).channel()
                    val connection: Connection = NetworkConnection(channel, this.pluginManager, this, host, port)
                    channel.attr(Attributes.ATTR_CONNECTION).set(connection)
                } else {
                    this.emit(ConnectionEvent.Error, ConnectionEventArgs(error = it.cause()))
                    workGroup.shutdownGracefully()
                }
            }


    }

    fun terminationFuture(): Future<*> {
        return workGroup.terminationFuture()
    }

    val connection: Connection get() = channel.attr(Attributes.ATTR_CONNECTION).get()

    fun shutdown(): Future<*> {
        this.emit(NetLifeCycleEvent.BeforeShutdown, this)
        if (workGroup.isShutdown) return FutureUtils.pass()
        return workGroup.shutdownGracefully()
    }

    fun sendPacket(packet: Packet): Future<*> {
        return connection.sendPacket(packet)
    }

    fun preInit(codecInitializer: CodecInitializer) {
        bootstrap.group(workGroup)
            .channel(NioSocketChannel::class.java)
            .option(ChannelOption.TCP_NODELAY, true)
            //.option(ChannelOption.SO_KEEPALIVE, true)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    try {
                        ch.pipeline().addLast(TIMEOUT_HANDLER, ReadTimeoutHandler(30))
                        codecInitializer.initChannel(ch, this@NetworkManager)
                        // 客户端事件处理
                        ch.pipeline().addLast(MANAGER_CHANNEL, ClientHandler(this@NetworkManager))
                        pluginManager.invokeHook(InitChannelPipelineHook, HookMessage(ch))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


    class Builder {
        private var host: String = "localhost"
        private var port = 25565
        private var protocolVersion: ProtocolVersion = ProtocolVersion.V1_12_2
        private var emitter: EventEmitter? = null
        private var pluginManager: IPluginManager = DummyPluginManager()
        private var codecInitializer: CodecInitializer = DummyCodecInitializer
        private var protocol: IPacketRegistry? = null
        private var channelRegistry: IChannelPacketRegistry? = null

        fun host(host: String): Builder {
            this.host = host
            return this
        }

        fun port(port: Int): Builder {
            this.port = port
            return this
        }

        fun protocolVersion(protocolVersion: ProtocolVersion): Builder {
            this.protocolVersion = protocolVersion
            return this
        }

        /**
         * 手动指定协议
         */
        fun protocol(registry: IPacketRegistry): Builder {
            this.protocol = registry
            return this
        }

        fun pluginManager(pluginManager: IPluginManager): Builder {
            this.pluginManager = pluginManager
            return this
        }

        fun eventEmitter(eventEmitter: EventEmitter): Builder {
            this.emitter = eventEmitter
            return this
        }


        fun codecInitializer(codecInitializer: CodecInitializer): Builder {
            this.codecInitializer = codecInitializer
            return this
        }

        fun build(): NetworkManager {
            val protocol = this.protocol ?: createProtocol(protocolVersion, pluginManager)
            val channelRegistry = this.channelRegistry ?: createChannel(protocolVersion)
            val emitter = DefaultEventEmitter()
            if (this.emitter != null) {
                this.emitter!!.targetTo(emitter)
                this.emitter!!.listenTo(emitter)
            }
            return NetworkManager(
                emitter,
                host,
                port,
                protocol,
                channelRegistry,
                pluginManager
            ).also {
                if (codecInitializer == DummyCodecInitializer) {
                    codecInitializer(DefaultClientCodecInitializer())
                }
                it.preInit(codecInitializer)
            }
        }
    }

}
