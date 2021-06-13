package top.limbang.doctor.client.event

import top.limbang.doctor.client.session.GameProfile
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.network.api.Connection
import top.limbang.doctor.protocol.definition.play.client.*

/**
 * ### 登陆成功事件
 */
object LoginSuccessEvent : Event<GameProfile>

