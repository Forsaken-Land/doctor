package top.fanua.doctor.plugin.astralsorcery.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:9:12
 */
data class PktLoginSyncGatewayPacket(
    override var messageId: Int = 0
) : FML2Packet

class PktLoginSyncGatewayDecoder : PacketDecoder<PktLoginSyncGatewayPacket> {
    override fun decoder(buf: ByteBuf): PktLoginSyncGatewayPacket {
        //不想处理x
        return PktLoginSyncGatewayPacket()
    }
}
