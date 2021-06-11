package top.limbang.doctor.network.event

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.core.NetworkManager
import top.limbang.doctor.network.event.ConnectionEvent.*
import top.limbang.doctor.network.event.NetLifeCycleEvent.BeforeConnect
import top.limbang.doctor.network.event.NetLifeCycleEvent.BeforeShutdown
import top.limbang.doctor.protocol.api.ProtocolState

/**
 * ### 连接事件
 * - [Connected] 连接成功
 * - [Disconnect] 断开连接
 * - [Read] 读取
 * - [Error] 错误
 */
enum class ConnectionEvent : Event<ConnectionEventArgs> {
    Connected,
    Disconnect,
    Read,
    Error
}

/**
 * ### 连接事件参数
 * - [context] 上下文
 * - [message] 消息
 * - [error] 错误消息
 */
data class ConnectionEventArgs(
    val context: ChannelHandlerContext? = null,
    val message: Any? = null,
    val error: Throwable? = null
)

/**
 * ### 网络生命周期事件
 * - [BeforeConnect] 连接前
 * - [BeforeShutdown] 关闭前
 */
enum class NetLifeCycleEvent : Event<NetworkManager> {
    BeforeConnect,
    BeforeShutdown
}

/**
 * ### 协议状态变更
 */
object ProtocolStateChange : Event<ProtocolStateChangeEventArgs>
/**
 * ### 协议状态变更事件参数
 * - [channel] 通道
 * - [from] 开始协议状态
 * - [to] 改变后协议状态
 */
data class ProtocolStateChangeEventArgs(
    val channel: Channel,
    val from: ProtocolState,
    val to: ProtocolState
)

