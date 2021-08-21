package top.fanua.doctor.protocol.version

import java.net.ProtocolException

/**
 * ### 协议版本
 */
enum class ProtocolVersion(val versionNumber: Int, val versionName: String) {
    V1_7_10(5, "1.7.10"),
    V1_12_2(340, "1.12.2"),
    V1_16_2(751, "1.16.2"),
    V1_16_5(754, "1.16.5"),
    V1_17_1(756, "1.17.1");

    companion object {
        private val protocolVersionMap = values().associateBy { it.versionNumber }
        fun fromNumber(versionNumber: Int) =
            protocolVersionMap[versionNumber] ?: throw ProtocolException("未知的协议版本号：$versionNumber")
    }
}
