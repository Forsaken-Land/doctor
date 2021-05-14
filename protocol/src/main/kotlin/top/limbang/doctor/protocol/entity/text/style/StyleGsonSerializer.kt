package top.limbang.doctor.protocol.entity.text.style

import com.google.gson.*
import top.limbang.doctor.protocol.utils.stringChild
import top.limbang.doctor.protocol.utils.stringChildNullable
import top.limbang.doctor.protocol.entity.text.IChat
import top.limbang.doctor.protocol.entity.text.style.*
import java.lang.reflect.Type

/**
 *
 * @author WarmthDawn
 * @since 2021-05-12
 */
object StyleGsonSerializer : JsonDeserializer<Style>, JsonSerializer<Style> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Style? {
        return if (json.isJsonObject) {
            Style().apply {
                val obj = json.asJsonObject
                if (obj.has("bold"))
                    bold = obj["bold"].asBoolean

                if (obj.has("italic")) {
                    italic = obj["italic"].asBoolean
                }
                if (obj.has("underlined")) {
                    underlined = obj["underlined"].asBoolean
                }
                if (obj.has("strikethrough")) {
                    strikethrough = obj["strikethrough"].asBoolean
                }
                if (obj.has("obfuscated")) {
                    obfuscated = obj["obfuscated"].asBoolean
                }
                if (obj.has("color")) {
                    color = TextFormatting.getFormat(obj.stringChild("color"));
                }
                if (obj.has("insertion")) {
                    insertion = obj["insertion"].asString
                }
                if (obj.has("clickEvent")) {
                    val event = obj.getAsJsonObject("clickEvent")
                    if (event != null) {
                        val action = ClickAction.getAction(event.stringChildNullable("action"))
                        val s = event.stringChildNullable("value")
                        if (action != null && s != null) {
                            clickEvent = ClickEvent(action, s)
                        }
                    }
                }
                if (obj.has("hoverEvent")) {
                    val event = obj.getAsJsonObject("hoverEvent")
                    if (event != null) {
                        val action = HoverAction.getAction(event.stringChildNullable("action"))
                        val chat: IChat? = context.deserialize(
                            event["value"],
                            IChat::class.java
                        )
                        if (action != null && chat != null) {
                            hoverEvent = HoverEvent(action, chat)
                        }
                    }
                }
            }
        } else {
            null
        }
    }

    override fun serialize(src: Style, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        TODO("Not yet implemented")
    }
}