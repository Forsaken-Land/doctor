package top.limbang.doctor.client.running.mod

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.utils.asObservable
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.plugin.laggoggles.definations.RequestScanPacket
import top.limbang.doctor.plugin.laggoggles.definations.ScanResultPacket
import top.limbang.doctor.plugin.laggoggles.definations.ScanResultPacket.Type.*
import top.limbang.doctor.plugin.laggoggles.entity.Entry
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:11:29
 */
class LagGogglesUtils(val client: MinecraftClient) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private val lagObservable = client.asObservable(PacketEvent(ScanResultPacket::class))
        .takeUntil {
            !it.hasMore
        }.map {
            it.data.map { objectData -> parseLagEntity(objectData) }
        }.toList()

    fun getLag(timeout: Long, unit: TimeUnit): List<LagEntity> {
        client.sendPacket(RequestScanPacket(20))
        val list = mutableListOf<LagEntity>()
        lagObservable.timeout(timeout, unit).toFuture().get()
            .forEach {
                it.forEach { lagEntity -> list.add(lagEntity) }
            }
        return list
    }

    fun getLag(): List<LagEntity> {
        return getLag(120, TimeUnit.SECONDS)
    }

    companion object {
        fun parseLagEntity(objectData: ScanResultPacket.ObjectData): LagEntity {
            return when (objectData.type) {
                ENTITY, TILE_ENTITY, GUI_ENTITY -> {
                    val lagData = Entity(
                        objectData.data[Entry.ENTITY_NAME] as String,
                        objectData.data[Entry.ENTITY_UUID] as UUID,
                        objectData.data[Entry.ENTITY_CLASS_NAME] as String
                    )
                    LagEntity(
                        objectData.data[Entry.WORLD_ID] as Int,
                        objectData.data[Entry.NANOS] as Long,
                        DateType.ENTITY,
                        lagData
                    )

                }

                BLOCK, GUI_BLOCK -> {
                    val lagData = Block(
                        objectData.data[Entry.BLOCK_NAME] as String,
                        objectData.data[Entry.BLOCK_POS_X] as Int,
                        objectData.data[Entry.BLOCK_POS_Y] as Int,
                        objectData.data[Entry.BLOCK_POS_Z] as Int,
                        objectData.data[Entry.BLOCK_CLASS_NAME] as String
                    )
                    LagEntity(
                        objectData.data[Entry.WORLD_ID] as Int,
                        objectData.data[Entry.NANOS] as Long,
                        DateType.BLOCK,
                        lagData
                    )

                }

                EVENT_BUS_LISTENER -> {
                    val lagData = Event(
                        objectData.data[Entry.EVENT_BUS_LISTENER] as String,
                        objectData.data[Entry.EVENT_BUS_THREAD_TYPE] as Int,
                        objectData.data[Entry.EVENT_BUS_EVENT_CLASS_NAME] as String
                    )
                    LagEntity(
                        objectData.data[Entry.WORLD_ID] as Int,
                        objectData.data[Entry.NANOS] as Long,
                        DateType.EVENT,
                        lagData
                    )
                }

            }
        }
    }
}

@Serializable
data class LagEntity(
    val worldId: Int,
    val nanos: Long,
    val dateType: DateType,
    val data: LagData
)

enum class DateType(id: Int) {
    ENTITY(0),
    BLOCK(1),
    EVENT(2);
}

interface LagData {
    val className: String
}

@Serializable
data class Entity(
    val name: String,
    val uuid: @Contextual UUID,
    override val className: String
) : LagData

@Serializable
data class Event(
    val listener: String,
    val threadType: Int,
    override val className: String
) : LagData

@Serializable
data class Block(
    val name: String,
    val x: Int,
    val y: Int,
    val z: Int,
    override val className: String
) : LagData
