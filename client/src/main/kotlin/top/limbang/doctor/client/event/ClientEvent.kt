package top.limbang.doctor.client.event

import top.limbang.doctor.client.session.GameProfile
import top.limbang.doctor.core.api.event.Event
import top.limbang.doctor.protocol.api.ProtocolState
import java.util.*

/**
 * ### 登陆成功事件
 */
object LoginSuccessEvent : Event<GameProfile>