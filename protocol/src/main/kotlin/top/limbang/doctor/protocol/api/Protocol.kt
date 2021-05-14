package top.limbang.doctor.protocol.api

import top.limbang.doctor.protocol.registry.IPacketRegistry

/**
 *
 * @author limbang
 * @since 2021-05-14
 */
interface Protocol : IPacketRegistry {
    /**
     * ### 协议接口
     */

    fun sendID(): Int
    fun readID(): Int

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
