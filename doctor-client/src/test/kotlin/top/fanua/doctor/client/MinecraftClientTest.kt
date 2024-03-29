package top.fanua.doctor.client

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.StringTag
import top.fanua.doctor.client.running.AutoVersionForgePlugin
import top.fanua.doctor.client.running.player.bag.PlayerBagPlugin
import top.fanua.doctor.client.running.player.bag.getPlayerBagUtils
import top.fanua.doctor.client.running.player.list.PlayerListPlugin
import top.fanua.doctor.client.running.player.list.getPlayerListTab
import top.fanua.doctor.client.running.player.status.PlayerStatusPlugin
import top.fanua.doctor.client.running.player.world.PlayerWorldPlugin
import top.fanua.doctor.client.running.player.world.getWorld
import top.fanua.doctor.client.running.tabcomplete.TabCompletePlugin
import top.fanua.doctor.client.running.tabcomplete.tabCompleteTool
import top.fanua.doctor.client.running.tps.TpsPlugin
import top.fanua.doctor.client.running.tps.tpsTools
import top.fanua.doctor.network.event.ConnectionEvent
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.fix.PluginFix
import top.fanua.doctor.plugin.forge.definations.fml1.Ids
import top.fanua.doctor.plugin.forge.definations.fml1.RegistryDataPacket
import top.fanua.doctor.plugin.ftbquests.PluginFtbQuests
import top.fanua.doctor.plugin.ftbquests.definations.MessageClaimAllRewardsPacket
import top.fanua.doctor.protocol.definition.play.client.ChatPacket
import top.fanua.doctor.protocol.definition.play.client.DisconnectPacket
import top.fanua.doctor.protocol.definition.play.client.EntityActionPacket
import top.fanua.doctor.protocol.definition.play.client.PlayerPositionAndLookPacket
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionAndLookPacket
import top.fanua.doctor.protocol.definition.play.server.CPlayerPositionPacket
import top.fanua.doctor.protocol.entity.text.ChatSerializer
import top.fanua.doctor.translation.api.I18n
import java.math.RoundingMode


@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // 离线登陆测试
//    val host = "localhost"
//    val port = 25565
//    val name = pros["name"] as String

    val client = MinecraftClient.builder()
        //.name(name)
        .user(username, password)
        .authServerUrl(authServerUrl)
        .sessionServerUrl(sessionServerUrl)
//        .plugin(PluginLagGoggles())
        .plugin(PlayerWorldPlugin())
        .plugin(PlayerStatusPlugin())
        .plugin(PluginFtbQuests())
        .plugin(PlayerListPlugin())
        .plugin(AutoVersionForgePlugin())
        .plugin(TabCompletePlugin())
        .plugin(TpsPlugin())
        .plugin(PlayerBagPlugin())
        .plugin(PluginFix())
        .build()

    if (!client.start(host, port)) return

    var x = 0
    var y = 0
    var z = 0
    val blocks = mutableListOf<Ids>()
    val items = mutableListOf<Ids>()
    client.on(ConnectionEvent.Disconnect) {
        Thread.sleep(1000L)
        client.reconnect()
    }.onPacket<RegistryDataPacket> {
        if (packet.name == "minecraft:blocks") {
            blocks.addAll(packet.ids)
        }
        if (packet.name == "minecraft:items") {
            items.addAll(packet.ids)
        }
    }
        .onPacket<ChatPacket> {
            if (!packet.json.contains("commands.forge.tps.summary")) {
                val chat = ChatSerializer.jsonToChat(packet.json)
                logger.info(chat.getFormattedText())
            }
        }.onPacket<DisconnectPacket> {
            val reason = ChatSerializer.jsonToChat(packet.reason)
            logger.warn(reason.getFormattedText())
        }.onPacket<PlayerPositionAndLookPacket> {
            val tempX = packet.x.toBigDecimal().setScale(0, RoundingMode.DOWN).toInt()
            y = packet.y.toInt()
            val tempZ = packet.z.toBigDecimal().setScale(0, RoundingMode.DOWN).toInt()
            x = if (tempX >= 0) tempX else tempX - 1
            z = if (tempZ >= 0) tempZ else tempZ - 1
        }
    var yaw = 0f
    GlobalScope.launch {
        launch {
            while (true) {
                when (val msg = readLine()) {
                    "tps" -> {
                        try {
                            val result = client.tpsTools.getTpsSuspend()
                            result.forEach {
                                logger.info("$it")
                            }
                        } catch (e: Exception) {
                            logger.error("获取tps失败", e)
                        }
                    }
                    "bag" -> {
                        client.getPlayerBagUtils.getBag().forEach { (t, u) ->
                            val name = items.find { u!!.blockID == it.id }?.name.orEmpty()
                            val list = I18n.DEFAULT.translateItem(
                                if (name.startsWith("minecraft")) name.replace("minecraft:", "item.") + "."
                                else "item.${name.replace(":", ".")}."
                            )
                            var item = I18n.DEFAULT.translate(
                                list.keys.toList().getOrNull(u!!.itemDamage!!)
                                    ?: (list.keys.first() + "name:${u.itemDamage}")
                            )
                            if (list.keys.first() == item) item += "name:${u.itemDamage}"
                            val nbt = u.nbt?.first()
                            if ((nbt?.key ?: "") == "SkullOwner") {
                                if (nbt?.value is StringTag) item =
                                    item.replace("%s", (nbt.value as StringTag).value)
                                else {
                                    if (nbt?.value is CompoundTag) {
                                        (nbt.value as CompoundTag).forEach { t, u ->
                                            if (t == "Name" && u is StringTag) item = item.replace("%s", u.value)

                                        }
                                    }
                                }
                            }
                            logger.info(
                                "位置$t:$item,数量:${u.itemCount}"
                            )
                        }
                    }
                    "list" -> {
                        val playerTab = client.getPlayerListTab()
                        logger.info("玩家数量:${playerTab.players.size}")
                        logger.info("更新时间:${playerTab.updateTime}")
                        playerTab.players.map {
                            logger.info("name:${it.value.name} ping:${it.value.ping} gameMode:${it.value.gameMode}")
                        }

                    }
                    "ftb" -> {
                        client.sendPacket(MessageClaimAllRewardsPacket())
                    }
                    else -> {
                        if (msg?.startsWith("/") == true) {
                            try {
                                client.tabCompleteTool.getCompletions(msg)
                            } catch (_: Exception) {
                            }
                        }
                        if (msg?.startsWith("丢") == true) {
                            try {
                                val id = msg.substring(1, msg.length).toIntOrNull() ?: 0
                                client.getPlayerBagUtils.dropItem(id)
                            } catch (_: Exception) {

                            }
                        } else if (!msg.isNullOrBlank()) {
                            client.sendMessage(msg)
                        }
                    }
                }

            }
        }
        launch {
            delay(15000)
            while (false) {
                try {
                    delay(1000)
                    client.sendPacket(EntityActionPacket(0, 1, 0))
                    delay(1000)
                    client.sendPacket(EntityActionPacket(0, 0, 0))
                } catch (_: Exception) {
                }
            }
        }
    }
    Thread.sleep(15000)
    while (true) {
        try {
            while (false) {
                Thread.sleep(50)
                if (yaw >= 360f) yaw = 0f
                else yaw += 0.5f

                val id = blocks.find { it.id == client.getWorld().getOrSet(x, y - 1, z).id }?.name ?: "minecraft:air"
                val id1 = blocks.find { it.id == client.getWorld().getOrSet(x, y - 2, z).id }?.name ?: "minecraft:air"

                if (id == "minecraft:air" || id.contains("torch", true)) {
                    if (id1 == "minecraft:air" || id.contains("torch", true)) {
                        client.sendPacket(
                            CPlayerPositionPacket(
                                x.toDouble() + 0.5,
                                y.toDouble() - 1,
                                z.toDouble() + 0.5,
                                false
                            )
                        )
                        y -= 1
                    } else {
                        client.sendPacket(
                            CPlayerPositionPacket(
                                x.toDouble() + 0.5,
                                y.toDouble() - 1,
                                z.toDouble() + 0.5,
                                false
                            )
                        )
                        y -= 1
                    }
                } else {
                    client.sendPacket(
                        CPlayerPositionAndLookPacket(
                            x.toDouble() + 0.5,
                            y.toDouble(),
                            z.toDouble() + 0.5,
                            yaw,
                            0f,
                            true
                        )
                    )
                }

            }
        } catch (_: Exception) {
        }
    }
}
