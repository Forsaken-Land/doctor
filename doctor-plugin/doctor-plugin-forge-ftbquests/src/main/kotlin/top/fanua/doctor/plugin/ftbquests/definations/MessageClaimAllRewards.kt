package top.fanua.doctor.plugin.ftbquests.definations

import io.netty.buffer.ByteBuf
import top.fanua.doctor.plugin.ftbquests.api.FtbQuestsPacket
import top.fanua.doctor.protocol.api.PacketEncoder

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:13:34
 */
class MessageClaimAllRewardsPacket : FtbQuestsPacket

class MessageClaimAllRewardsEncoder : PacketEncoder<MessageClaimAllRewardsPacket> {
    override fun encode(buf: ByteBuf, packet: MessageClaimAllRewardsPacket): ByteBuf {
        return buf
    }
}
