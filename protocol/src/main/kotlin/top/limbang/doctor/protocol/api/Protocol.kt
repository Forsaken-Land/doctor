package top.limbang.doctor.protocol.api

/**
 *
 * @author limbang
 * @since 2021-05-14
 */
interface Protocol {
    /**
     * ### 协议接口
     */

    fun sendID(): Int
    fun readID(): Int

    /**
     * 基于协议包ID和协议状态,查询协议包解码[PacketDecoder]
     */
    fun <T : Packet> queryOriginalPacketDecoder(packetId: Int, state: ProtocolState): PacketDecoder<T>

    /**
     * 基于协议包[Packet]查询协议包编码[PacketEncoder]
     */
    fun <T : Packet> queryOriginalPacketEncoder(packet: T): PacketEncoder<T>

    /**
     * 基于协议包[Packet]查询协议包 ID
     */
    fun <T : Packet> queryOriginalPacketId(packet: T): Int
}

/**
 * ## 协议状态
 *
 * - [HANDSHAKE] 初始握手状态
 * - [STATUS] 握手后状态为1切换
 * - [LOGIN]握手后状态为2切换
 * - [PLAY]登录成功后切换
 */
enum class ProtocolState {
    HANDSHAKE,
    STATUS,
    LOGIN,
    PLAY
}
