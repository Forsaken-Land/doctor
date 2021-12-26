package top.fanua.doctor.protocol.registry

import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.ProtocolState
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_12_2


/**
 * 表示这个接口提供包注册
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IPacketRegistry : GroupRegistrable<IPacketRegistry> {
    /**
     * 获取[dir]方向，在[state]状态下的包注册表
     */
    fun packetMap(dir: PacketDirection, state: ProtocolState): IVanillaPacketMap

    /**
     * 获取[dir]方向，在[state]状态下的包注册表，同时使用kotlin函数写法
     */
    fun packetMap(dir: PacketDirection, state: ProtocolState, action: IVanillaPacketMap.() -> Unit) =
        packetMap(dir, state).run(action)

    /**
     * 柯里化之后的包注册方法，参考 [MinecraftClientProtocol_v1_12_2]
     *
     */
    fun packetMap(state: ProtocolState, action: DirectionAction.() -> Unit) = DirectionAction(this, state).run(action)

}

/**
 * 这只是支持函数式写法的玩意
 */
class DirectionAction(private val registry: IPacketRegistry, private val state: ProtocolState) {

    /**
     * 客户端->服务端的数据包
     */
    fun whenC2S(action: IVanillaPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.C2S, state))
    }

    fun whenS2C(action: IVanillaPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.S2C, state))
    }
}

/**
 * 原版数据包的注册表
 */
typealias IVanillaPacketMap = IPacketMap<Int, Packet>
