package top.limbang.doctor.plugin.forge

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.hooks.InitChannelPipelineHook
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.codec.ForgePacketHandler
import top.limbang.doctor.plugin.forge.handler.ForgeHandshakeListener
import top.limbang.doctor.plugin.forge.protocol.FML1
import top.limbang.doctor.protocol.entity.ServiceResponse

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class FML1Plugin(
    val modList: List<ServiceResponse.Mod>,
) : Plugin,
    EventEmitter by DefaultEventEmitter() {


    override fun created() {
    }

    override fun destroy() {
    }

    /**
     * 注册插件的事件
     */
    override fun registerEvent(emitter: EventEmitter) {
        emitter.addListener(ForgeHandshakeListener(this))
    }

    override fun hookProvider(registry: IHookProviderRegistry) {
        registry.provider(InitChannelPipelineHook::class.java).addHook {
            this.pipeline().addBefore(
                "clientHandler", "fml1:clientHandler",
                ForgePacketHandler(this@FML1Plugin, FML1()) //TODO: 这个handler逻辑或许得改
            )

            this.attr(ATTR_FORGE_STATE).set(ForgeProtocolState.REGISTER)
        }
    }

}