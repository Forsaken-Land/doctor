package top.limbang.doctor.plugin.silentgear.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:24
 */
data class SyncTraitsPacket(
    override var messageId: Int = 0
) : FML2Packet


class SyncTraitsPacketDecoder : PacketDecoder<SyncTraitsPacket> {
    override fun decoder(buf: ByteBuf): SyncTraitsPacket {
        return SyncTraitsPacket()
    }
}
