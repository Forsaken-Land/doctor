package top.fanua.doctor.protocol.entity.text

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import top.fanua.doctor.protocol.entity.text.style.StyleSerializer
import top.fanua.doctor.protocol.utils.stringChild
import top.fanua.doctor.protocol.utils.stringChildNullable
import top.fanua.doctor.translation.api.I18n

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
object ChatSerializer {
    fun deserialize(json: JsonElement): IChat = deserialize(json, I18n.DEFAULT)
    fun deserialize(json: JsonElement, i18n: I18n): IChat {
        return when {
            (json is JsonPrimitive) -> {
                PlainChat(json.content)
            }
            (json !is JsonObject) -> {
                if (json is JsonArray) {
                    json.map {
                        deserialize(it)
                    }.reduce { acc, el ->
                        acc.also { it.appendSibling(el) }
                    }
                } else {
                    throw SerializationException("Don't know how to turn $json into a Component")
                }
            }
            else -> {
                val obj = json
                when {
                    //纯文本
                    obj.contains("text") -> {
                        PlainChat(obj["text"]!!.jsonPrimitive.content)
                    }
                    //翻译文本
                    obj.contains("translate") -> {
                        val s = obj["translate"]!!.jsonPrimitive.content
                        if (obj.contains("with")) {
                            val args = obj["with"]!!.jsonArray.map { arg ->
                                deserialize(arg)
                                    .let {
                                        if (it is PlainChat && it.style.isEmpty && it.getSiblings().isEmpty()) {
                                            it.text
                                        } else {
                                            it
                                        }
                                    }
                            }.toTypedArray()
                            TranslationChat(s, args, i18n)
                        } else {
                            TranslationChat(s, i18n = i18n)
                        }
                    }
                    //计分板文本
                    obj.contains("score") -> {
                        val scoreObj = obj["score"]!!.jsonObject
                        if (!scoreObj.contains("name") || !scoreObj.contains("objective")) {
                            throw SerializationException("A score component needs a least a name and an objective")
                        }
                        ScoreChat(
                            scoreObj.stringChild("name"),
                            scoreObj.stringChild("objective"),
                            scoreObj.stringChildNullable("value")
                        )
                    }
                    //选择器文本
                    obj.contains("selector") -> {
                        SelectorChat(obj.stringChild("selector"))
                    }
                    //按键绑定文本
                    obj.contains("keybind") -> {
                        KeybindChat(obj.stringChild("keybind"))
                    }
                    else -> {
                        throw SerializationException("Don't know how to turn $json into a Component")
                    }
                }.also { result ->
                    if (obj.contains("extra")) {
                        obj["extra"]?.jsonArray?.forEach {
                            result.appendSibling(deserialize(it))
                        }
                    }
                    result.style = StyleSerializer.deserialize(json)!!
                }
            }
        }
    }

    fun jsonToChat(json: String): IChat {
        return deserialize(Json.parseToJsonElement(json))
    }

}
