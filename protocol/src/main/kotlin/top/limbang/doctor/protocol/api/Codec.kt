package top.limbang.doctor.protocol.api

import io.netty.buffer.ByteBuf
import top.limbang.doctor.protocol.extension.*

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
interface PacketDecoder<out T : Packet> : PacketCodec {
    /**
     * 解码
     */
    fun decoder(buf: ByteBuf): T
}

/**
 * ### 协议包编码
 */
interface PacketEncoder<in T : Packet> : PacketCodec {
    /**
     * 编码
     */
    fun encode(buf: ByteBuf, packet: T): ByteBuf
}