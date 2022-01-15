package top.fanua.doctor.plugin.fix.handler

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.running.player.bag.getPlayerBagUtils
import top.fanua.doctor.client.running.player.status.getPlayerStatus
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.forge.definations.fml1.Ids
import top.fanua.doctor.plugin.forge.definations.fml1.RegistryDataPacket
import top.fanua.doctor.protocol.definition.play.client.SetSlotPacket
import top.fanua.doctor.protocol.definition.play.client.WindowItemsPacket

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/7 9:29
 */
@OptIn(DelicateCoroutinesApi::class)
class TombManyGravesFix(client: MinecraftClient) {
    private val items = mutableListOf<Ids>()
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val bugName = "tombmanygraves:death_list"

    init {
        val scope = GlobalScope
        log.info("TombManyGravesFix插件启动")
        client.onPacket<RegistryDataPacket> {
            if (packet.name == "minecraft:items") {
                items.addAll(packet.ids)
            }
        }.onPacket<WindowItemsPacket> {
            scope.launch {
                delay(100)
                while (client.getPlayerStatus().heal <= 0.0) {
                    delay(10)
                }
                packet.slotData.forEach { (id, data) ->
                    val name = items.find { it.id == data.blockID }?.name.orEmpty()
                    if (name == bugName) {
                        log.info("检测到死亡清单:$data")
                        delay(50)
                        client.getPlayerBagUtils.dropItem(id, 1)
                    }
                }
            }
        }.onPacket<SetSlotPacket> {
            if (packet.windowId == 0) {
                scope.launch {
                    delay(100)
                    while (client.getPlayerStatus().heal <= 0.0) {
                        delay(10)
                    }
                    val name = items.find { it.id == packet.slotData.blockID }?.name.orEmpty()
                    if (name == bugName) {
                        log.info("检测到死亡清单:${packet.slotData}")
                        delay(100)
                        client.getPlayerBagUtils.dropItem(packet.slot, 1)
                    }
                }
            }
        }
    }
}
