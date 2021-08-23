package top.fanua.doctor.plugin.environmental.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/8/24:0:36
 */
data class SyncSweaterTypeMessagePacket(
    override var messageId: Int = 0
) : FML2Packet

class SyncSweaterTypeMessageDecoder : PacketDecoder<SyncSweaterTypeMessagePacket> {
    override fun decoder(buf: ByteBuf) = SyncSweaterTypeMessagePacket()
}
