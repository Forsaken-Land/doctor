package top.limbang.doctor.network.api.handler

import io.netty.channel.ChannelHandlerContext
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.api.client.Client
import top.limbang.doctor.network.api.handler.event.ReadPacketEventArgs

/**
 *
 * @author Doctor_Yin
 * @since 2021/5/14:20:42
 */
object ConnectionSucceededEvent : Event<ChannelHandlerContext>
object DisconnectEvent : Event<Client>
object ConnectionFailed : Event<Throwable>
object ReadPacketEvent : Event<ReadPacketEventArgs>