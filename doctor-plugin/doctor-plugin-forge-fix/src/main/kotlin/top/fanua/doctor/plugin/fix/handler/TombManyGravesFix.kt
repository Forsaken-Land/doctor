package top.fanua.doctor.plugin.fix.handler

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.client.running.player.bag.getPlayerBagUtils
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.plugin.forge.definations.fml1.Ids
import top.fanua.doctor.plugin.forge.definations.fml1.RegistryDataPacket

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/7 9:29
 */
@OptIn(DelicateCoroutinesApi::class)
class TombManyGravesFix(client: MinecraftClient) {
    private val items = mutableListOf<Ids>()
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    init {

        log.info("TombManyGravesFix插件启动")
        client.onPacket<RegistryDataPacket> {
            if (packet.name == "minecraft:items") {
                items.addAll(packet.ids)
            }
        }
        GlobalScope.launch {
            delay(1000 * 10)
            val bagUtils = client.getPlayerBagUtils
            while (true) {
                delay(1000)
                try {
                    bagUtils.getBag().forEach { (t, u) ->
                        val name = items.find { (u?.blockID ?: 0) == it.id }?.name.orEmpty()
                        if (name == "tombmanygraves:death_list") {
                            log.info("检测到死亡清单:$u")
                            bagUtils.dropItem(t, 1)
                        }
                    }
                } catch (e: Exception) {
                    log.warn(e.message)
                }
            }
        }
    }
}