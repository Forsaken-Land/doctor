package top.limbang.doctor.plugin.forge.registry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
interface IForgePacketGroup {
    fun registerPackets(registry: IForgePacketRegistry)
}