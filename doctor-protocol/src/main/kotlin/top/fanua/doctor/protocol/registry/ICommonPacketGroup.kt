package top.fanua.doctor.protocol.registry

import top.fanua.doctor.protocol.utils.cast

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface ICommonPacketGroup<T> {
    fun registerPackets(registry: T)
}

interface GroupRegistrable<T> {
    fun registerGroup(group: ICommonPacketGroup<T>) {
        group.registerPackets(this.cast())
    }
}
