package top.fanua.doctor.plugin.silentgems.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 21:05
 */
data class SyncSoulsPacket(
    override var messageId: Int = 0
) : FML2Packet

class SyncSoulsPacketDecoder : PacketDecoder<SyncSoulsPacket> {
    override fun decoder(buf: ByteBuf): SyncSoulsPacket {
        //懒
        return SyncSoulsPacket()
    }
}