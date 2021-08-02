package top.fanua.doctor.plugin.extendedcrafting.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:49
 */
data class SyncSingularitiesMessagePacket(
    override var messageId: Int = 0
) : FML2Packet

class SyncSingularitiesMessageDecoder : PacketDecoder<SyncSingularitiesMessagePacket> {
    override fun decoder(buf: ByteBuf): SyncSingularitiesMessagePacket {
        return SyncSingularitiesMessagePacket()
    }
}
