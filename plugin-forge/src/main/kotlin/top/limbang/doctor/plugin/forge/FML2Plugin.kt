package top.limbang.doctor.plugin.forge

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.hooks.InitChannelPipelineHook
import top.limbang.doctor.plugin.forge.codec.Forge2PacketHandler
import top.limbang.doctor.plugin.forge.handler.Forge2HandshakeListener
import top.limbang.doctor.plugin.forge.protocol.FML2


/**
 *
 * @author Doctor_Yin
 * @since 2021/5/16 下午9:50
 */
class FML2Plugin(
    val modList: Map<String, String>,
) : Plugin,
    EventEmitter by DefaultEventEmitter() {

    private val channelPacketRegistry = FML2()

    override fun created() {
    }

    override fun destroy() {
    }

    /**
     * 注册插件的事件
     */
    override fun registerEvent(emitter: EventEmitter) {
           emitter.addListener(Forge2HandshakeListener(this))
    }

    override fun hookProvider(registry: IHookProviderRegistry) {
        registry.provider(InitChannelPipelineHook::class.java).addHook {
            this.pipeline().addBefore(
                "clientHandler", "fml2:clientHandler",
                Forge2PacketHandler(this@FML2Plugin, channelPacketRegistry) //TODO: 这个handler逻辑或许得改
            )
        }

    }

}
