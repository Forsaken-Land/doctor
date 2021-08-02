package top.fanua.doctor.plugin.forge

import top.fanua.doctor.core.api.event.EventEmitter
import top.fanua.doctor.core.api.plugin.IPluginHookManager
import top.fanua.doctor.core.api.plugin.Plugin
import top.fanua.doctor.core.impl.event.DefaultEventEmitter
import top.fanua.doctor.core.plugin.addHandler
import top.fanua.doctor.network.hooks.InitChannelPipelineHook
import top.fanua.doctor.plugin.forge.codec.Forge2PacketHandler
import top.fanua.doctor.plugin.forge.handler.Forge2HandshakeListener
import top.fanua.doctor.plugin.forge.protocol.FML2
import top.fanua.doctor.plugin.forge.registry.IModPacketRegistry
import top.fanua.doctor.plugin.forge.registry.ModPacketRegistryImpl


/**
 *
 * @author Doctor_Yin
 * @since 2021/5/16 下午9:50
 */
class FML2Plugin(
    val modList: Map<String, String>
) : Plugin,
    EventEmitter by DefaultEventEmitter() {
    val modRegistry: IModPacketRegistry = ModPacketRegistryImpl()
    val channelPacketRegistry = FML2()

    /**
     * 注册插件的事件
     */
    override fun registerEvent(emitter: EventEmitter) {
        emitter.addListener(Forge2HandshakeListener(this))
    }

    override fun registerHook(manager: IPluginHookManager) {
        manager.getHook(InitChannelPipelineHook).addHandler(this) {
            val channel = it.message
            channel.pipeline().addBefore(
                "clientHandler", "fml2:clientHandler",
                Forge2PacketHandler(this, channelPacketRegistry) //TODO: 这个handler逻辑或许得改
            )
            false
        }
    }

}
