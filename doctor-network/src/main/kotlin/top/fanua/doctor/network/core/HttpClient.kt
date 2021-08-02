package top.fanua.doctor.network.core

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.*
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.charset.StandardCharsets


@Serializable
data class HttpResponse(val code: Int, val content: String)

/**
 * http 客户端
 *
 * @author limbang-pc
 * @CreateTime 2021-01-26
 */
class HttpClient {
    private val logger: Logger = LoggerFactory.getLogger(HttpClient::class.java)

    private val bootstrap = Bootstrap()
    private val group = NioEventLoopGroup()
    private lateinit var httpResponse: HttpResponse

    constructor() {
        val ssl = SslContextBuilder
            .forClient()
            .trustManager(InsecureTrustManagerFactory.INSTANCE)
            .build()
        bootstrap.group(group)
            .channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<SocketChannel>() {
                override fun initChannel(ch: SocketChannel) {
                    ch.pipeline().addLast(ssl.newHandler(ch.alloc()))
                    ch.pipeline().addLast(HttpResponseDecoder())
                    ch.pipeline().addLast(HttpRequestEncoder())
                    ch.pipeline().addLast(HttpObjectAggregator(65535))
                    ch.pipeline().addLast(object : SimpleChannelInboundHandler<FullHttpResponse>() {
                        override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpResponse) {
                            val code = msg.status().code()
                            val content = msg.content().toString(StandardCharsets.UTF_8)
                            httpResponse = HttpResponse(code, content)
                            logger.debug("HTTP[响应]:$code - $content")
                            ch.close()
                        }
                    })
                }
            })
    }

    fun postJson(url: String, body: String): HttpResponse {
        val uri = URI(url)
        val host = uri.host
        val port = if (uri.port == -1) if ("http" == uri.scheme!!) 80 else 443 else uri.port
        val request = DefaultFullHttpRequest(
            HttpVersion.HTTP_1_1, HttpMethod.POST, uri.rawPath,
            Unpooled.wrappedBuffer(body.toByteArray(StandardCharsets.UTF_8))
        )
        request.headers()[HttpHeaderNames.HOST] = host
        request.headers()[HttpHeaderNames.CONTENT_LENGTH] = request.content().readableBytes()
        request.headers()[HttpHeaderNames.CONTENT_TYPE] = "application/json"
        // 连接
        val channelFuture = bootstrap.connect(host, port).sync()
        logger.debug("HTTP[请求]:${request.uri()} - $body")
        // 发送数据
        channelFuture.channel().writeAndFlush(request)
        // 等待通道关闭
        channelFuture.channel().closeFuture().sync()
        return httpResponse
    }

    fun close() {
        // 释放线程组
        group.shutdownGracefully()
    }

    companion object {
        fun postJson(url: String, body: String): HttpResponse {
            val client = HttpClient()
            try {
                return client.postJson(url, body)
            } finally {
                client.close()
            }
        }
    }
}

