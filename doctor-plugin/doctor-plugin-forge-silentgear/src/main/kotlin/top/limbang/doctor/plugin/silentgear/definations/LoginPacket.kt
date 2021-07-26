package top.limbang.doctor.plugin.silentgear.definations

import io.netty.buffer.ByteBuf
import top.limbang.doctor.plugin.forge.api.FML2Packet
import top.limbang.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/7/27:3:21
 */
data class LoginPacket(
    override var messageId: Int
) : FML2Packet


class LoginPacketEncoder : PacketEncoder<LoginPacket> {
    override fun encode(buf: ByteBuf, packet: LoginPacket): ByteBuf {
        return buf
    }
}
