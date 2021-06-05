package top.limbang.doctor.plugin.forge.api

import top.limbang.doctor.protocol.api.ChannelPacket
import top.limbang.doctor.protocol.api.LoginPlugin
import top.limbang.doctor.protocol.api.Packet


interface ModPacket : Packet {
    val channel: String
}

interface FML1Packet : ChannelPacket
interface FML2Packet : LoginPlugin {
    override var messageId: Int
}
