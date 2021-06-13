package top.limbang.doctor.network.hooks

import io.netty.channel.Channel
import top.limbang.doctor.core.api.plugin.HookMessage
import top.limbang.doctor.core.api.plugin.IPluginHookProvider
import top.limbang.doctor.core.api.plugin.MutableHookMessage
import top.limbang.doctor.protocol.api.Packet

/**
 *
 * @author WarmthDawn
 * @since 2021-05-14
 */

object InitChannelPipelineHook : IPluginHookProvider<HookMessage<Channel>>
object BeforePacketSendHook : IPluginHookProvider<MutableHookMessage<Packet>>

