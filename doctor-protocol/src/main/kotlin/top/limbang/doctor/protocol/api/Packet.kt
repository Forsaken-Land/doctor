package top.limbang.doctor.protocol.api

/**
 * ### 协议包
 */
interface Packet
interface ChannelPacket : Packet
interface LoginPlugin : Packet {
    var messageId: Int
}
