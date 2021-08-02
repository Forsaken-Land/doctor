package top.fanua.doctor.protocol.registry

import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.core.PacketDirection
import top.fanua.doctor.protocol.version.vanilla.MinecraftClientProtocol_v1_12_2

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/3 下午3:59
 */
interface IChannelPacketRegistry : GroupRegistrable<IChannelPacketRegistry> {
    /**
     * 获取[dir]方向下的包注册表
     */
    fun packetMap(dir: PacketDirection): IChannelPacketMap

    /**
     * 获取[dir]方向下的包注册表，同时使用kotlin函数写法
     */
    fun packetMap(dir: PacketDirection, action: IChannelPacketMap.() -> Unit) =
        packetMap(dir).run(action)

    /**
     * 柯里化之后的包注册方法，参考 [MinecraftClientProtocol_v1_12_2]
     *
     */
    fun packetMap(action: DirectionActionChannel.() -> Unit) = DirectionActionChannel(this).run(action)
}

/**
 * 这只是支持函数式写法的玩意
 */
class DirectionActionChannel(private val registry: IChannelPacketRegistry) {

    /**
     * 客户端->服务端的数据包
     */
    fun whenC2S(action: IChannelPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.C2S))
    }

    fun whenS2C(action: IChannelPacketMap.() -> Unit) {
        action(registry.packetMap(PacketDirection.S2C))
    }
}

/**
 * 插件通道数据包的注册表
 */
typealias IChannelPacketMap = IPacketMap<String, Packet>
