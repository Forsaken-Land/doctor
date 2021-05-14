package top.limbang.doctor.network.api.client

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.AttributeKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.network.api.handler.ClientHandler
import top.limbang.doctor.network.api.handler.Connection
import top.limbang.doctor.network.api.handler.ConnectionFailed
import top.limbang.doctor.network.api.handler.NetworkConnection
import top.limbang.doctor.network.api.handler.coder.ProtocolPacketCoder
import top.limbang.doctor.network.api.handler.coder.VarIntLengthBasedFrameCodec
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.play.server.ChatPacketC
import top.limbang.doctor.protocol.registry.IPacketRegistry
import top.limbang.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_12_2

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:20:38
 */
class Client : EventEmitter by DefaultEventEmitter() {
    private val logger: Logger = LoggerFactory.getLogger(Client::class.java)

    private val workGroup = NioEventLoopGroup()
    private val bootstrap = Bootstrap()
    private lateinit var channel: Channel

    private var host = "127.0.0.1"
    private var port = 25565
    private var protocol: IPacketRegistry = MinecraftClientProtocol_v1_12_2(PluginManager(emitter))

    /**
     * 设置服务器地址,默认为 127.0.0.1
     *
     * @param host 地址
     */
    fun host(host: String): Client {
        this.host = host
        return this
    }

    /**
     * 设置服务器端口,默认为 25565
     *
     * @param port 端口
     */
    fun port(port: Int): Client {
        this.port = port
        return this
    }

    /**
     * 设置协议版本,默认为 [MinecraftClientProtocol_v1_12_2]
     *
     * @param protocol 协议版本
     */
    fun protocol(protocol: IPacketRegistry): Client {
        this.protocol = protocol
        return this
    }

    fun sendPacket(packet: Packet) {
        val conn = channel.attr(AttributeKey.valueOf<Connection>("connection")).get()
        if (conn.protocolState == ProtocolState.PLAY) {
            conn.sendPacket(packet)
        }
    }

    fun sendMessage(msg: String?) {
        sendPacket(ChatPacketC(msg ?: ""))
    }

    /**
     * ### 启动客户端
     *
     * #### 处理流程如下:
     * 入站: encryptionCoder -> varIntLengthBasedFrameCoder -> compressionCoder -> protocolPacketCoder
     * -> inboundPacketHandler(触发读取协议包事件)
     *
     * 出站: protocolPacketCoder -> compressionCoder -> varIntLengthBasedFrameCoder -> encryptionCoder
     *
     */
    fun start() {
        try {
            bootstrap.group(workGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, true)
                //.option(ChannelOption.SO_KEEPALIVE, true)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel) {
                        // 协议包处理流程
                        ch.pipeline().addLast("encryptionCoder", ChannelInboundHandlerAdapter())
                        ch.pipeline().addLast("varIntLengthBasedFrameCoder", VarIntLengthBasedFrameCodec())
                        ch.pipeline().addLast("compressionCoder", ChannelInboundHandlerAdapter())
                        ch.pipeline().addLast("protocolPacketCoder", ProtocolPacketCoder(protocol))

                        // 客户端事件处理
                        ch.pipeline().addLast("clientHandler", ClientHandler(this@Client))
                    }
                })
            // 连接服务器
            doConnect(ProtocolState.HANDSHAKE)
        } catch (e: Exception) {
            logger.error("启动客户端出现错误.", e)
        }
    }

    /**
     * ### 重新连接服务器
     *
     * @param state 连接时的状态
     */
    fun doConnect(state: ProtocolState) {

        if (this::channel.isInitialized && channel.isActive) return
        val future = bootstrap.connect(host, port)
        future.addListener {
            if (it.isSuccess) {
                channel = future.channel()
                val connection: Connection =
                    NetworkConnection(channel, host, port, state)
                channel.attr(AttributeKey.valueOf<Connection>("connection")).set(connection)
            } else {
                this.emit(ConnectionFailed, it.cause())
                workGroup.shutdownGracefully()
            }
        }
    }

    /**
     * ### 关闭客户端线程组
     *
     */
    fun shutdown() {
        if (workGroup.isShutdown) return
        workGroup.shutdownGracefully()
    }

}