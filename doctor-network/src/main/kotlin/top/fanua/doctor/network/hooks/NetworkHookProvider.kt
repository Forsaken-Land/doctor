package top.fanua.doctor.network.hooks

import io.netty.channel.Channel
import top.fanua.doctor.core.api.plugin.HookMessage
import top.fanua.doctor.core.api.plugin.IPluginHookProvider
import top.fanua.doctor.core.api.plugin.MutableHookMessage
import top.fanua.doctor.protocol.api.Packet

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

object InitChannelPipelineHook : IPluginHookProvider<HookMessage<Channel>>
object BeforePacketSendHook : IPluginHookProvider<MutableHookMessage<Packet>>

