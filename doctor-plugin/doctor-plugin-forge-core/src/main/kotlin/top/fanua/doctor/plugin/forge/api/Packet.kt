package top.fanua.doctor.plugin.forge.api

import top.fanua.doctor.protocol.api.ChannelPacket
import top.fanua.doctor.protocol.api.LoginPlugin
import top.fanua.doctor.protocol.api.Packet


interface ModPacket : Packet {
    val channel: String
}

interface FML1Packet : ChannelPacket
interface FML2Packet : LoginPlugin {
    override var messageId: Int
}
