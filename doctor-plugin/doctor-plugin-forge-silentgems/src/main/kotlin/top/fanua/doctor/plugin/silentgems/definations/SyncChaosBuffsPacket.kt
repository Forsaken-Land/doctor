package top.fanua.doctor.plugin.silentgems.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 21:10
 */
data class SyncChaosBuffsPacket(
    override var messageId: Int = 0
) : FML2Packet

class SyncChaosBuffsPacketDecoder : PacketDecoder<SyncChaosBuffsPacket> {
    override fun decoder(buf: ByteBuf): SyncChaosBuffsPacket {
        return SyncChaosBuffsPacket()
    }
}

