package top.fanua.doctor.protocol.utils

import top.fanua.doctor.core.tryGetGenericType
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.core.ProtocolException

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */


fun <T : Packet> PacketEncoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Encoder对应的class，请手动指定。")
}

fun <T : Packet> PacketDecoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Decoder对应的class，请手动指定。")
}

fun <T : Any> Any.cast(): T {
    return this as T
}
