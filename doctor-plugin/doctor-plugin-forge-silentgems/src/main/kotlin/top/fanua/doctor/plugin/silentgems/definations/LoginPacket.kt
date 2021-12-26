package top.fanua.doctor.plugin.silentgems.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.forge.api.FML2Packet
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2021/12/26 21:07
 */
data class LoginPacket(
    override var messageId: Int = 0
) : FML2Packet

class LoginPacketEncoder : PacketEncoder<LoginPacket> {
    override fun encode(buf: ByteBuf, packet: LoginPacket): ByteBuf {
        return buf
    }
}