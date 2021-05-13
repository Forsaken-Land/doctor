package top.limbang.doctor.protocol.api

import top.limbang.doctor.core.api.protocol.IPacketBuffer

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
    fun decoder(buf: IPacketBuffer): T
}

/**
 * ### 协议包编码
 */
interface PacketEncoder<in T : Packet> : PacketCodec {
    /**
     * 编码
     */
    fun encode(buf: IPacketBuffer, packet: T): IPacketBuffer
}