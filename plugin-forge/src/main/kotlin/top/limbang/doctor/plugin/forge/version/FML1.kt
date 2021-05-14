package top.limbang.doctor.plugin.forge.version

import top.limbang.doctor.plugin.forge.api.ForgeProtocol
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.registry.ForgePacketRegistryImpl
import top.limbang.doctor.plugin.forge.registry.IForgePacketRegistry

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
class FML1() :
    ForgeProtocol, IForgePacketRegistry by ForgePacketRegistryImpl() {
    override val sendId: Int = 10
    override val readId: Int = 10

    init {
        channelPacketMap(ForgeProtocolState.HANDSHAKE) {
            whenC2S {

            }
        }

        modPacketMap("botania") {
            with(ForgeProtocolState.STATUS) {

            }

            with(ForgeProtocolState.PLAY) {
                whenS2C {

                }
            }
        }

    }
}