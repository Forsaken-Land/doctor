package top.fanua.doctor.client.utils

import kotlinx.serialization.json.*
import top.fanua.doctor.client.entity.ForgeFeature
import top.fanua.doctor.client.entity.ForgeInfo
import top.fanua.doctor.client.entity.ServerInfo

/**
 * ### 服务器信息工具类
 */
object ServerInfoUtils {

    fun getServiceInfo(json: String): ServerInfo {
        val jsonElement = Json.parseToJsonElement(json)

        // 获取版本信息
        val versionObject = jsonElement.jsonObject["version"]!!
        val versionNumber = versionObject.jsonObject["protocol"]!!.jsonPrimitive.int
        val versionName = versionObject.jsonObject["name"]!!.jsonPrimitive.content
        // 获取描述
        val descriptionObject = jsonElement.jsonObject["description"]!!
        val description = try {
            descriptionObject.jsonObject["text"]?.jsonPrimitive?.content ?: descriptionObject.jsonPrimitive.content
        } catch (e: IllegalArgumentException) {
            descriptionObject.jsonPrimitive.content
        }


        // 获取玩家信息
        val playerObject = jsonElement.jsonObject["players"]!!
        val playerMax = playerObject.jsonObject["max"]!!.jsonPrimitive.int
        val playerOnline = playerObject.jsonObject["online"]!!.jsonPrimitive.int
        val playerNameList = mutableListOf<String>()
        playerObject.jsonObject["sample"]?.jsonArray?.forEach {
            playerNameList.add(it.jsonObject["name"]!!.jsonPrimitive.content)
        }
        // 获取forge信息
        val forge = getForge(jsonElement)
        // 获取mod数
        val modNumber = forge?.modMap?.size ?: 0

        return ServerInfo(
            description,
            playerMax,
            playerOnline,
            playerNameList,
            versionName,
            versionNumber,
            forge,
            modNumber
        )
    }

    /**
     * ### 获取 Forge 信息
     */
    private fun getForge(jsonElement: JsonElement): ForgeInfo? {
        val forgeFeature = getForgeFeature(jsonElement) ?: return null
        val modStr: String
        val modIdStr: String
        val modVersionStr: String

        when (forgeFeature) {
            ForgeFeature.FML1 -> {
                modStr = "modList"
                modIdStr = "modid"
                modVersionStr = "version"
            }
            ForgeFeature.FML2 -> {
                modStr = "mods"
                modIdStr = "modId"
                modVersionStr = "modmarker"
            }
        }

        val modMap = mutableMapOf<String, String>()
        val modArray = jsonElement.jsonObject[forgeFeature.getFeature()]!!.jsonObject[modStr]!!.jsonArray

        modArray.forEach {
            val id = it.jsonObject[modIdStr]!!.jsonPrimitive.content
            val version = it.jsonObject[modVersionStr]!!.jsonPrimitive.content
            modMap[id] = version
        }

        return ForgeInfo(forgeFeature, modMap)
    }

    /**
     * ### 获取 Forge 特征
     */
    private fun getForgeFeature(jsonElement: JsonElement): ForgeFeature? {
        ForgeFeature.values().forEach {
            val feature = it.getFeature()
            val forgeObj = jsonElement.jsonObject[feature]
            if (forgeObj != null) {
                return it
            }
        }
        return null
    }
}
