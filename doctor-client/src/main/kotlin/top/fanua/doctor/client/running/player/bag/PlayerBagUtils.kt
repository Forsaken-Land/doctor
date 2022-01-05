package top.fanua.doctor.client.running.player.bag

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.fanua.doctor.client.MinecraftClient
import top.fanua.doctor.network.handler.onPacket
import top.fanua.doctor.network.handler.replyPacket
import top.fanua.doctor.protocol.definition.play.client.SConfirmTransactionPacket
import top.fanua.doctor.protocol.definition.play.client.SetSlotPacket
import top.fanua.doctor.protocol.definition.play.client.SlotData
import top.fanua.doctor.protocol.definition.play.client.WindowItemsPacket
import top.fanua.doctor.protocol.definition.play.server.CCloseWindowPacket
import top.fanua.doctor.protocol.definition.play.server.CConfirmTransactionPacket
import top.fanua.doctor.protocol.definition.play.server.ClickMode
import top.fanua.doctor.protocol.definition.play.server.ClickWindowPacket

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/5 14:17
 */
class PlayerBagUtils(private val client: MinecraftClient) {
    private val bag = mutableMapOf<Int, SlotData?>()
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    init {
        client.onPacket<WindowItemsPacket> {
            if (packet.windowsId == 0) packet.slotData.forEach { (t, u) -> bag[t] = u }
        }.onPacket<SetSlotPacket> {
            if (packet.windowId == 0) bag[packet.slot] = packet.slotData
        }.replyPacket<SConfirmTransactionPacket> {
            CConfirmTransactionPacket(it.windowId, it.actionNumber, it.accepted)
        }
    }

    fun getBag() = bag.filter { it.value != null }.filter { it.value!!.blockID != -1 }

    /**
     * 丢弃指定格子全部
     *
     * [id] 格子
     *
     * @return 此格子是否有
     */
    fun dropItem(id: Int): Boolean {
        val data = bag[id]
        return if (data != null && data.blockID != -1) {
            bag.remove(id)
            client.sendPacket(ClickWindowPacket(0, id, 1, 1, ClickMode.DROP, data))
            client.sendPacket(CCloseWindowPacket(0))
            true
        } else false
    }

    /**
     * 丢弃指定格子指定数量
     *
     * [id] 格子
     *
     * [size] 数量
     *
     * @return 丢弃数量
     */
    fun dropItem(id: Int, size: Int): Int {
        val data = bag[id]
        return if (data?.itemCount != null && data.blockID != -1) {
            var dropId = 1
            if (data.itemCount!! > size) {
                var copy = data.itemCount!!
                for (i in 0 until size) {
                    client.sendPacket(ClickWindowPacket(0, id, 0, dropId++, ClickMode.DROP, data))
                    copy -= 1
                }
                bag[id] = data.copy(itemCount = copy)
                size
            } else {
                for (i in 0 until data.itemCount!!) {
                    client.sendPacket(ClickWindowPacket(0, id, 0, dropId++, ClickMode.DROP, data))
                }
                bag.remove(id)
                data.itemCount!!
            }.also {
                client.sendPacket(CCloseWindowPacket(0))
            }
        } else 0
    }
}