package top.limbang.doctor.plugin.astralsorcery.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:9:14
 */
data class PktLoginSyncPerkInformationPacket(
    override var messageId: Int = 0
) : FML2Packet

class PktLoginSyncPerkInformationDecoder : PacketDecoder<PktLoginSyncPerkInformationPacket> {
    override fun decoder(buf: ByteBuf): PktLoginSyncPerkInformationPacket {
        //不想处理x
        return PktLoginSyncPerkInformationPacket()
    }
}
