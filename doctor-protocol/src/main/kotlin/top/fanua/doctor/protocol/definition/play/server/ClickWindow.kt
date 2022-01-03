package top.fanua.doctor.protocol.definition.play.server

import io.netty.buffer.ByteBuf
import kotlinx.serialization.Serializable
import top.fanua.doctor.protocol.api.Packet
import top.fanua.doctor.protocol.api.PacketEncoder
import top.fanua.doctor.protocol.definition.play.client.SlotData
import top.fanua.doctor.protocol.extension.writeSlotData
import top.fanua.doctor.protocol.extension.writeVarInt

/**
 *
 * @author Doctor_Yin
 * @since 2022/1/2:15:00
 */
/**
 * This packet is sent by the player when it clicks on a slot in a window.
 *
 * [windowId] The ID of the window which was clicked. 0 for player inventory.
 *
 * [slot] The clicked slot number
 *
 * [button] The button used in the click
 *
 * [actionNumber] A unique number for the action, implemented by Notchian as a counter, starting at 1 (different counter for every window ID). Used by the server to send back a Confirm Transaction (clientbound).
 *
 * [mode] Inventory operation mode
 *
 * [slot] The clicked slot. Has to be empty (item ID = -1) for drop mode.
 */
@Serializable
data class ClickWindowPacket(
    val windowId: Int,
    val slot: Int,
    val button: Int,
    val actionNumber: Int,
    val mode: ClickMode,
    val clickedItem: SlotData
) : Packet

class ClickWindowEncoder : PacketEncoder<ClickWindowPacket> {
    override fun encode(buf: ByteBuf, packet: ClickWindowPacket): ByteBuf {
        buf.writeByte(packet.windowId and 0xFF)
        buf.writeShort(packet.slot)
        buf.writeByte(packet.button)
        buf.writeShort(packet.actionNumber)
        buf.writeVarInt(packet.mode.id)
        buf.writeSlotData(packet.clickedItem)
        return buf
    }
}

enum class ClickMode(val id: Int) {
    MOUSE_CLICK(0),
    SHIFT_MOUSE_CLICK(1),
    NUMBER_KEY(2),
    MIDDLE_CLICK(3),
    DROP(4),
    MOUSE_DRAG(5),
    DOUBLE_CLICK(6);

    fun getValue(id: Int): ClickMode {
        return values().find { it.id == id } ?: MOUSE_CLICK
    }
}
