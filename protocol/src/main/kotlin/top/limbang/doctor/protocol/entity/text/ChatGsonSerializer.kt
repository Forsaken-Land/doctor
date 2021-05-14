package top.limbang.doctor.protocol.entity.text

import com.google.gson.*
import top.limbang.doctor.protocol.utils.stringChild
import top.limbang.doctor.protocol.utils.stringChildNullable
import top.limbang.doctor.protocol.entity.text.style.Style
import top.limbang.doctor.protocol.entity.text.style.StyleGsonSerializer
import java.lang.reflect.Type

/**
 *
 * @author WarmthDawn
 * @since 2021-05-11
 */
object ChatGsonSerializer : JsonDeserializer<IChat>, JsonSerializer<IChat> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IChat {
        return when {
            (json.isJsonPrimitive) -> {
                PlainChat(json.asString)
            }
            (!json.isJsonObject) -> {
                if (json.isJsonArray) {
                    json.asJsonArray.map {
                        deserialize(it, it.javaClass, context)
                    }.reduce { acc, el ->
                        acc.also { it.appendSibling(el) }
                    }
                } else {
                    throw JsonParseException("Don't know how to turn $json into a Component")
                }
            }
            else -> {
                val obj: JsonObject = json.asJsonObject
                when {
                    //纯文本
                    obj.has("text") -> {
                        PlainChat(obj["text"].asString)
                    }
                    //翻译文本
                    obj.has("translate") -> {
                        val s = obj["translate"].asString
                        if (obj.has("with")) {
                            val args = obj.getAsJsonArray("with").map { arg ->
                                deserialize(arg, typeOfT, context)
                                    .let {
                                        if (it is PlainChat && it.style.isEmpty && it.getSiblings().isEmpty()) {
                                            it.text
                                        } else {
                                            it
                                        }
                                    }
                            }.toTypedArray()
                            TranslationChat(s, args)
                        } else {
                            TranslationChat(s)
                        }
                    }
                    //计分板文本
                    obj.has("score") -> {
                        val scoreObj = obj.getAsJsonObject("score")
                        if (!scoreObj.has("name") || !scoreObj.has("objective")) {
                            throw JsonParseException("A score component needs a least a name and an objective")
                        }
                        ScoreChat(
                            scoreObj.stringChild("name"),
                            scoreObj.stringChild("objective"),
                            scoreObj.stringChildNullable("value")
                        )
                    }
                    //选择器文本
                    obj.has("selector") -> {
                        SelectorChat(obj.stringChild("selector"))
                    }
                    //按键绑定文本
                    obj.has("keybind") -> {
                        KeybindChat(obj.stringChild("keybind"))
                    }
                    else -> {
                        throw JsonParseException("Don't know how to turn $json into a Component")
                    }
                }.also { result ->
                    if (obj.has("extra")) {
                        obj.getAsJsonArray("extra").forEach {
                            result.appendSibling(deserialize(it, typeOfT, context))
                        }
                    }
                    result.style = context.deserialize(json, Style::class.java)
                }
            }
        }
    }

    override fun serialize(src: IChat, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        TODO("Not yet implemented")
    }

    val GSON: Gson = GsonBuilder()
        .registerTypeHierarchyAdapter(IChat::class.java, this)
        .registerTypeHierarchyAdapter(Style::class.java, StyleGsonSerializer)
//        .registerTypeAdapterFactory(EnumTypeAdapterFactory())
        .create()


    fun jsonToChat(json: String): IChat {
        return GSON.getAdapter(IChat::class.java).fromJson(json)
    }
}