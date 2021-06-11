package top.limbang.doctor.protocol.api

import top.limbang.doctor.protocol.api.ProtocolState.*
import java.net.ProtocolException

/**
 * ## 协议状态
 *
 * - [HANDSHAKE] 初始握手状态
 * - [STATUS] 握手后状态为1切换
 * - [LOGIN]握手后状态为2切换
 * - [PLAY]登录成功后切换
 */
enum class ProtocolState(val id: Int) {
    HANDSHAKE(-1),
    PLAY(0),
    STATUS(1),
    LOGIN(2);

    companion object {
        private val stateMap = values().associateBy { it.id }
        fun fromId(id: Int) = stateMap[id] ?: throw ProtocolException("未知的ProtocolState：$id")
    }
}
