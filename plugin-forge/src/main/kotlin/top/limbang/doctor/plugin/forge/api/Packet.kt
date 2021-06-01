package top.limbang.doctor.plugin.forge.api

import top.limbang.doctor.protocol.api.Packet


interface ModPacket : Packet
interface ChannelPacket : Packet
interface FML1Packet : ChannelPacket
interface FML2Packet : ChannelPacket {
    var messageId: Int
}
