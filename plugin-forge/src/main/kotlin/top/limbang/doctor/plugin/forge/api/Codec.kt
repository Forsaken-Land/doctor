package top.limbang.doctor.plugin.forge.api

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.api.PacketDecoder
import top.limbang.doctor.protocol.api.PacketEncoder
import top.limbang.doctor.protocol.core.ProtocolException
import top.limbang.doctor.core.tryGetGenericType

/**
 *
 * @author limbang
 * @since 2021-05-14
 */

/**
 * ### 协议包编解码
 *
 * 所有编解码协议都不应该实现此 [PacketCodec] 接口,
 * 应实现 [PacketDecoder] 和 [PacketEncoder] 接口
 * 如需编解码应该继承 [PacketCodec]
 */
interface PacketCodec

/**
 * ### 协议包解码
 */
interface ForgePacketDecoder<out T : ForgePacketBase> : PacketCodec {
    /**
     * 解码
     */
    fun decoder(buf: ByteBuf): T
}

/**
 * ### 协议包编码
 */
interface ForgePacketEncoder<in T : ForgePacketBase> : PacketCodec {
    /**
     * 编码
     */
    fun encode(buf: ByteBuf, packet: T): ByteBuf
}


fun <T : ForgePacketBase> ForgePacketEncoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Encoder对应的class，请手动指定。")
}
fun <T : ForgePacketBase> ForgePacketDecoder<T>.packetClass(): Class<T> {
    return tryGetGenericType(this.javaClass) ?: throw ProtocolException("未能成功注册Decoder对应的class，请手动指定。")
}