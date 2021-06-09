package top.limbang.doctor.plugin.forge

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.IPluginManager
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.handler.ReadPacketListener
import top.limbang.doctor.network.hooks.InitChannelPipelineHook
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.codec.Forge1PacketHandler
import top.limbang.doctor.plugin.forge.codec.FML1ModPacketHandler
import top.limbang.doctor.plugin.forge.handler.Forge1HandshakeListener
import top.limbang.doctor.plugin.forge.protocol.FML1
import top.limbang.doctor.plugin.forge.registry.IModPacketRegistry
import top.limbang.doctor.plugin.forge.registry.ModPacketRegistryImpl

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class FML1Plugin(
    val modList: Map<String, String>
) : Plugin,
    EventEmitter by DefaultEventEmitter() {
    val modRegistry: IModPacketRegistry = ModPacketRegistryImpl()
    val channelPacketRegistry = FML1()

    override fun created(manager: IPluginManager) {
    }

    override fun destroy() {
    }

    /**
     * 注册插件的事件
     */
    override fun registerEvent(emitter: EventEmitter) {
        emitter.addListener(ReadPacketListener())
        emitter.addListener(Forge1HandshakeListener(this))
    }

    override fun hookProvider(registry: IHookProviderRegistry) {
        registry.provider(InitChannelPipelineHook::class.java).addHook {
            this.pipeline().addBefore(
                "clientHandler", "fml1:clientHandler",
                Forge1PacketHandler(this@FML1Plugin, channelPacketRegistry) //TODO: 这个handler逻辑或许得改
            )
            this.pipeline().addAfter(
                "fml1:clientHandler", "fml1:modHandler",
                FML1ModPacketHandler(modRegistry)
            )

            this.attr(ATTR_FORGE_STATE).set(ForgeProtocolState.HELLO)
        }

//        registry.provider(BeforePacketSendHook::class.java).addHook {
//            if (packet is HandshakePacket) {
//                val old = packet as HandshakePacket
//                modified = true
//                packet = HandshakePacket(
//                    old.version,
//                    old.address + "\u0000FML\u0000",
//                    old.port,
//                    old.state
//                )
//            }
//        }
    }

}
