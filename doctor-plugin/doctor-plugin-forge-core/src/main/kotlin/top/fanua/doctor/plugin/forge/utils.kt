package top.fanua.doctor.plugin.forge

import io.netty.channel.ChannelHandlerContext
import io.netty.util.AttributeKey
import top.fanua.doctor.plugin.forge.api.ForgeProtocolState

/**
 *
 * @author WarmthDawn
 * @since 2021-05-15
 */
val ATTR_FORGE_STATE = AttributeKey.valueOf<ForgeProtocolState>("forge:protocol_state")
fun ChannelHandlerContext.forgeProtocolState(): ForgeProtocolState {
    return this.channel().attr(ATTR_FORGE_STATE).get()
}

