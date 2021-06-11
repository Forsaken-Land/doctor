package top.limbang.doctor.protocol.entity.text.style

import kotlinx.serialization.json.*
import top.limbang.doctor.protocol.entity.text.ChatSerializer
import top.limbang.doctor.protocol.entity.text.IChat
import top.limbang.doctor.protocol.utils.stringChild
import top.limbang.doctor.protocol.utils.stringChildNullable

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
object StyleSerializer {

    fun deserialize(json: JsonElement): Style? {
        return if (json is JsonObject) {
            Style().apply {
                if (json.contains("bold"))
                    bold = json["bold"].asBoolean

                if (json.contains("italic")) {
                    italic = json["italic"].asBoolean
                }
                if (json.contains("underlined")) {
                    underlined = json["underlined"].asBoolean
                }
                if (json.contains("strikethrough")) {
                    strikethrough = json["strikethrough"].asBoolean
                }
                if (json.contains("obfuscated")) {
                    obfuscated = json["obfuscated"].asBoolean
                }
                if (json.contains("color")) {
                    color = TextFormatting.getFormat(json.stringChild("color"));
                }
                if (json.contains("insertion")) {
                    insertion = json["insertion"].takeIf { it is JsonPrimitive }?.jsonPrimitive?.content
                }
                if (json.contains("clickEvent")) {
                    val event = json["clickEvent"]
                    if (event is JsonObject) {
                        val action = ClickAction.getAction(event.stringChildNullable("action"))
                        val s = event.stringChildNullable("value")
                        if (action != null && s != null) {
                            clickEvent = ClickEvent(action, s)
                        }
                    }
                }
                if (json.contains("hoverEvent")) {
                    val event = json["hoverEvent"]
                    if (event is JsonObject) {
                        val action = HoverAction.getAction(event.stringChildNullable("action"))
                        if (event.contains("value")) {
                            val chat: IChat = ChatSerializer.deserialize(event["value"]!!)
                            if (action != null) {
                                hoverEvent = HoverEvent(action, chat)
                            }
                        }
                    }
                }
            }
        } else {
            null
        }
    }
}

val JsonElement?.asBoolean: Boolean? get() = this.takeIf { this is JsonPrimitive }?.jsonPrimitive?.booleanOrNull
