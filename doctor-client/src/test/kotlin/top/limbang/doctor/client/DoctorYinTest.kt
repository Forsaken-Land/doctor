package top.limbang.doctor.client

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.limbang.doctor.client.running.mod.*
import top.limbang.doctor.network.handler.oncePacket
import top.limbang.doctor.protocol.definition.play.client.JoinGamePacket

fun main() {

    val client = MinecraftClient()
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
        .enablePlayerList()

    if (!client.start(host, port)) return

    client.enableLag()

    client.oncePacket<JoinGamePacket> {
        GlobalScope.launch {
            val lags = client.getLagSuspend()
            var outMsg = "[123]高于1000us/t 的方块/实体如下:\n"
            var size = 0
            lags.forEach {
//                if (size > 50) return@forEach
                if (it.nanos > 1000 * 1000) { //可能是这样换算
                    size++
                    outMsg += when (it.data) {
                        is Block -> {
                            val name = (it.data as Block).name
                            val x = (it.data as Block).x
                            val y = (it.data as Block).y
                            val z = (it.data as Block).z
                            val worldId = (it.data as Block).worldId
                            "方块：$name,位置：$worldId($x,$y,$z),耗时：${it.nanos / 1000}us/t\n" //可能是这样换算
                        }
                        is Entity -> {
                            val name = (it.data as Entity).name
                            val uuid = (it.data as Entity).uuid
                            val worldId = (it.data as Entity).worldId
                            "实体：$name,uuid：$uuid,时间：$worldId,耗时：${it.nanos / 1000}us/t\n" //可能是这样换算
                        }
                        is Event -> {
                            ""
                        }
                    }
                }
            }
            logger.info(outMsg)
            client.stop()
        }
    }
}