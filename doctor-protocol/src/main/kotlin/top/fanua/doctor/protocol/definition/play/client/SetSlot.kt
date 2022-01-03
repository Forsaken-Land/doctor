package top.fanua.doctor.protocol.definition.play.client

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketDecoder
import top.fanua.doctor.protocol.extension.readSlotData

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:14:41
 */
/**
 * Sent by the server when an item in a slot (in a window) is added/removed.
 *
 * [windowId] The window which is being updated. 0 for player inventory. Note that all known window types include the player inventory. This packet will only be sent for the currently opened window while the player is performing actions, even if it affects the player inventory. After the window is closed, a number of these packets are sent to update the player's inventory window (0).
 *
 * [slot] The slot that should be updated
 *
 * To set the cursor (the item currently dragged with the mouse), use -1 as Window ID and as Slot.
 *
 * This packet can only be used to edit the hotbar of the player's inventory if window ID is set to 0 (slots 36 through 44). If the window ID is set to -2, then any slot in the inventory can be used but no add item animation will be played.
 */
@Serializable
data class SetSlotPacket(
    val windowId: Int,
    val slot: Int,
    val slotData: SlotData
) : Packet

class SetSlotDecoder : PacketDecoder<SetSlotPacket> {
    override fun decoder(buf: ByteBuf): SetSlotPacket {
        val windowId = buf.readByte().toInt()
        val slot = buf.readShort().toInt()
        val slotData = buf.readSlotData()
        return SetSlotPacket(windowId, slot, slotData)
    }
}
