package top.limbang.doctor.protocol.core.annotation

import top.limbang.doctor.protocol.api.Packet
import kotlin.reflect.KClass

/**
 * 表示这个包是兼容不同版本的拓展类
 * @author WarmthDawn
 * @since 2021-06-13
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class VersionExpandPacket(
    val parent: KClass<out Packet>
)
