package top.fanua.doctor.plugin.forge.definations.fml2

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.plugin.forge.entity.Snapshot
import top.fanua.doctor.plugin.forge.entity.readSnapshot
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readString

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/1 下午8:01
 */
@Serializable
data class ServerRegisterPacket(
    val registerName: String,
    val snapshotPresent: Boolean,
    @Contextual
    val snapshot: Snapshot? = null,
    override var messageId: Int = 0
) : FML2Packet

class ServerRegisterDecoder : PacketDecoder<ServerRegisterPacket> {
    override fun decoder(buf: ByteBuf): ServerRegisterPacket {
        val registerName = buf.readString()
        val snapshotPresent = buf.readBoolean()
        return if (snapshotPresent) {
            ServerRegisterPacket(registerName, snapshotPresent, buf.readSnapshot())
        } else ServerRegisterPacket(registerName, snapshotPresent)
    }
}
