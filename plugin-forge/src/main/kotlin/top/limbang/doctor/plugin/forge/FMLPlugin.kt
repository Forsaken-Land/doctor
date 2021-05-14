package top.limbang.doctor.plugin.forge

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import top.limbang.doctor.core.api.IHookProviderRegistry
import top.limbang.doctor.core.api.event.EventEmitter
import top.limbang.doctor.core.api.plugin.Plugin
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.network.hooks.InitChannelHook
import top.limbang.doctor.protocol.api.Packet
import top.limbang.doctor.protocol.api.ProtocolState
import top.limbang.doctor.protocol.definition.play.client.CustomPayloadDecoder
import top.limbang.doctor.protocol.definition.play.client.CustomPayloadPacket
import top.limbang.doctor.protocol.extension.readString
import top.limbang.doctor.protocol.hook.PacketRegistryHook

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */
class FMLPlugin : Plugin {
    override fun created() {
    }

    override fun destroy() {
    }

    override fun registerEvent(emitter: EventEmitter) {
        emitter.on(PacketEvent(CustomPayloadPacket::class)) {
            when (it.channel) {
                "MC|Brand" -> {
                    it.data.readString()
                }
            }
        }
    }

    override fun hookProvider(registry: IHookProviderRegistry) {
        registry.provider(PacketRegistryHook::class.java).addHook {
            packetMap(ProtocolState.PLAY) {
                whenC2S {
                    register(0x00, CustomPayloadDecoder())
                }
            }
        }

        registry.provider(InitChannelHook::class.java).addHook {
            //
            this.addBefore("clientHandler", "fml:clientHandler", FMLHandler());
        }
    }

    class FMLHandler : SimpleChannelInboundHandler<Packet>() {
        override fun channelRead0(ctx: ChannelHandlerContext, msg: Packet) {
            var handled = false
            when (msg) {
                is CustomPayloadPacket -> {
                    when (msg.channel) {
                        "MC|Brand" -> {
                            msg.data.readString()
                            handled = true
                        }
                    }
                }
            }
            //包没有处理，交给下一个codec
            if(!handled) {
                ctx.fireChannelRead(msg)
            }
        }

    }
}