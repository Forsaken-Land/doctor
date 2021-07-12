package top.limbang.doctor.plugin.astralsorcery.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketDecoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/12:8:34
 */
data class PktLoginSyncDataHolderPacket(
//    val syncData: Map<ResourceLocation, CompoundTag>,
    override var messageId: Int = 0
) : FML2Packet

class PktLoginSyncDataHolderDecoder : PacketDecoder<PktLoginSyncDataHolderPacket> {
    override fun decoder(buf: ByteBuf): PktLoginSyncDataHolderPacket {

        //不想处理了x
//        val size = buf.readInt()
//        val syncData = mutableMapOf<ResourceLocation, CompoundTag>()
//        for (i in 0 until size) {
//            syncData[buf.readResourceLocation()] = buf.readCompoundTag()
//        }
        return PktLoginSyncDataHolderPacket()
    }
}
