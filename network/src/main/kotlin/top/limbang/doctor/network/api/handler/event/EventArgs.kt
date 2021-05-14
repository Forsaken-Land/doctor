package top.limbang.doctor.network.api.handler.event

import io.netty.channel.Channel
import top.limbang.doctor.protocol.api.Packet

/**
 *
 * @author WarmthDawn
 * @since 2021-05-13
 */

data class ReadPacketEventArgs(val packet: Packet, val channel: Channel)