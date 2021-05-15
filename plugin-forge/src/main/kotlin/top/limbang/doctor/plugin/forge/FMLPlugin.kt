package top.limbang.doctor.plugin.forge

import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.core.impl.event.DefaultEventEmitter
import top.limbang.doctor.network.hooks.InitChannelPipelineHook
import top.limbang.doctor.plugin.forge.api.ForgeProtocolState
import top.limbang.doctor.plugin.forge.handler.ForgeHandshakeListener
import top.limbang.doctor.protocol.entity.ServiceResponse

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class FMLPlugin : Plugin,
    EventEmitter by DefaultEventEmitter() {
    lateinit var modList: List<ServiceResponse.Mod>

    override fun created() {
    }

    override fun destroy() {
    }

    override fun registerEvent(emitter: EventEmitter) {
        emitter.addListener(ForgeHandshakeListener(this))
    }

    override fun hookProvider(registry: IHookProviderRegistry) {
        registry.provider(InitChannelPipelineHook::class.java).addHook {
            this.pipeline().addBefore("clientHandler", "fml:clientHandler", null)

            this.attr(ATTR_FORGE_STATE).set(ForgeProtocolState.REGISTER)
        }
    }

}