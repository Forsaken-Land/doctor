package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.querz.nbt.tag.CompoundTag
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readSlotData

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:1:53
 */
/**
 * Sent by the server when items in multiple slots (in a window) are added/removed. This includes the main inventory, equipped armour and crafting slots.
 *
 * [windowsId] The ID of window which items are being sent for. 0 for player inventory.
 *
 * [count] Short Number of elements in the following array
 */
@Serializable
data class WindowItemsPacket(
    val windowsId: Int,
    val count: Int,
    val slotData: Map<Int, SlotData>
) : Packet

class WindowItemsDecoder : PacketDecoder<WindowItemsPacket> {
    override fun decoder(buf: ByteBuf): WindowItemsPacket {
        val windowsId = buf.readUnsignedByte().toInt()
        val count = buf.readShort().toInt()
        val slotData = mutableMapOf<Int, SlotData>()
        for (i in 0 until count) {
            slotData[i] = buf.readSlotData()
        }
        return WindowItemsPacket(windowsId, count, slotData)
    }
}

@Serializable
data class SlotData(
    val blockID: Int,
    val itemCount: Int? = null,
    val itemDamage: Int? = null,
    val nbt: @Contextual CompoundTag? = null
)
