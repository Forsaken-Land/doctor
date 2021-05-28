package top.limbang.doctor.client.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import top.limbang.doctor.core.plugin.PluginManager
import top.limbang.doctor.plugin.forge.FML1Plugin
import top.limbang.doctor.plugin.forge.FML2Plugin
import top.limbang.doctor.protocol.entity.ServiceResponse

/**
 * ### Minecraft 自动工具
 */
object AutoUtils {
    /**
     * ### 自动识别版本
     * [jsonStr] Ping 返回的 Json 信息
     */
    fun autoVersion(jsonStr: String): String {
        val obj = toJsonObj(jsonStr)
        //原版
        return obj.getAsJsonObject("version").getAsJsonPrimitive("name").asString!!
    }

    fun autoProtocol(jsonStr: String): Int {
        val obj = toJsonObj(jsonStr)
        //原版
        return obj.getAsJsonObject("version").getAsJsonPrimitive("protocol").asInt
    }

    /**
     * ### 自动识别 Forge 版本，并注册插件
     */
    fun autoForgeVersion(jsonStr: String, pluginManager: PluginManager?): String {
        val obj = toJsonObj(jsonStr)
        // 自动判断特征
        ForgeFeature.values().forEach {
            val feature = it.getFeature()
            val forgeObj = obj.getAsJsonObject(feature)
            if (forgeObj != null) {
                if (pluginManager != null) registerFML(it, forgeObj, pluginManager)
                return it.getForgeVersion()
            }
        }
        return ""
    }

    /**
     * ### 自动识别 Forge 版本
     */
    fun autoForgeVersion(jsonStr: String): String {
        return autoForgeVersion(jsonStr, null)
    }

    /**
     * ### 获取协议版本
     */
    fun getProtocolVersion(jsonStr: String): Int {
        val obj = toJsonObj(jsonStr)
        return obj.getAsJsonObject("version").getAsJsonPrimitive("protocol").asInt
    }

    /**
     * ### 注册 FML 插件
     */
    private fun registerFML(forge: ForgeFeature, forgeObj: JsonObject, pluginManager: PluginManager) {
        return when (forge) {
            ForgeFeature.FML1 -> {
                val modList = Gson().fromJson(forgeObj, ServiceResponse.Modinfo::class.java).modList
                pluginManager.registerPlugin(FML1Plugin(modList))
            }
            ForgeFeature.FML2 -> {
                val modList = Gson().fromJson(forgeObj, ServiceResponse.ForgeData::class.java).mods
                pluginManager.registerPlugin(FML2Plugin(modList))
            }
        }
    }

    /**
     * ### 解析 Json
     */
    private fun toJsonObj(jsonStr: String): JsonObject {
        val json = JsonParser.parseString(jsonStr)
        if (!json.isJsonObject) {
            throw JsonParseException("服务器的返回值不是一个json对象")
        }
        return json.asJsonObject
    }


    /**
     * ### Forge 特征
     */
    private enum class ForgeFeature {
        FML1, FML2;

        /**
         * ### 获取特征
         */
        fun getFeature(): String {
            return when (this) {
                FML1 -> "modinfo"
                FML2 -> "forgeData"
            }
        }

        /**
         * ### 获取 Forge 版本
         */
        fun getForgeVersion(): String {
            return when (this) {
                FML1 -> "\u0000FML\u0000"
                FML2 -> "\u0000FML2\u0000"
            }
        }
    }
}
