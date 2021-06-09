package top.limbang.doctor.client.running.mod

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import top.limbang.doctor.client.MinecraftClient
import top.limbang.doctor.client.entity.ForgeFeature
import top.limbang.doctor.client.running.DummyTpsTools
import top.limbang.doctor.client.running.ITpsTools
import top.limbang.doctor.client.running.TpsEntity
import top.limbang.doctor.client.running.TpsToolsFML1
import top.limbang.doctor.client.session.UUIDSerializer
import top.limbang.doctor.client.utils.asObservable
import top.limbang.doctor.network.handler.PacketEvent
import top.limbang.doctor.plugin.laggoggles.PluginLagGoggles
import top.limbang.doctor.plugin.laggoggles.definations.RequestScanPacket
import top.limbang.doctor.plugin.laggoggles.definations.ScanResultPacket
import top.limbang.doctor.plugin.laggoggles.definations.ScanResultPacket.Type.*
import top.limbang.doctor.plugin.laggoggles.entity.Entry
import top.limbang.doctor.protocol.definition.play.client.ChatType0Packet
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * @author Doctor_Yin
 * @since 2021/6/6:11:29
 */
private lateinit var lagTools: LagTools

fun MinecraftClient.enableLag(): MinecraftClient {
    pluginManager.registerPlugin(PluginLagGoggles)
    lagTools = LagTools(this)
    return this
}

class LagException(msg: String) : RuntimeException(msg)

fun MinecraftClient.getLag(): Future<List<LagEntity>> {
    return if (::lagTools.isLateinit) {
        lagTools.getLag()
    } else throw LagException("客户端未启用Lag")

}

suspend fun MinecraftClient.getLagSuspend(): List<LagEntity> {
    return if (::lagTools.isLateinit) {
        lagTools.getLagSuspend()
    } else throw LagException("客户端未启用Lag")
}

interface ILagTools {
    val defaultTimeout: Pair<Long, TimeUnit>
    fun getLag(
        timeout: Long = defaultTimeout.first, unit: TimeUnit = defaultTimeout.second,
        callback: (error: Throwable?, List<LagEntity>) -> Unit
    ): Disposable

    suspend fun getLagSuspend(
        timeout: Long = defaultTimeout.first,
        unit: TimeUnit = defaultTimeout.second
    ): List<LagEntity> =
        suspendCancellableCoroutine { cont ->
            val disposable = getLag(timeout, unit) { err, it ->
                if (err != null) {
                    cont.resumeWithException(err)
                } else {
                    cont.resume(it)
                }
            }
            cont.invokeOnCancellation {
                if (!disposable.isDisposed) {
                    disposable.dispose()
                }
            }
        }

    fun getLag(timeout: Long = defaultTimeout.first, unit: TimeUnit = defaultTimeout.second): Future<List<LagEntity>> {
        return Single.create<List<LagEntity>?> { obs ->
            getLag(timeout, unit) { err, it ->
                if (err != null) {
                    obs.onError(err)
                } else {
                    obs.onSuccess(it)
                }
            }
        }.toFuture()
    }
}

class LagTools(
    val client: MinecraftClient,
    timeout: Long = 3,
    timeUnit: TimeUnit = TimeUnit.MINUTES
) : ILagTools {
    override val defaultTimeout: Pair<Long, TimeUnit> = Pair(timeout, timeUnit)
    private val lagObservable = client.asObservable(PacketEvent(ScanResultPacket::class))
        .observeOn(Schedulers.io())
        .takeUntil {
            !it.hasMore
        }.map {
            it.data.map { objectData ->
                parseLagEntity(objectData)
            }
        }.toList()

    override fun getLag(
        timeout: Long,
        unit: TimeUnit,
        callback: (error: Throwable?, List<LagEntity>) -> Unit
    ): Disposable {
        val result = lagObservable.timeout(timeout, unit)
            .subscribe { it, err ->
                if (err != null) {
                    callback(err, emptyList())
                } else {
                    val list = mutableListOf<LagEntity>()
                    it.forEach {
                        it.forEach { lagEntity ->
                            if (lagEntity != null) {
                                list.add(lagEntity)
                            }
                        }
                    }
                    callback(null, list)
                }
            }
        client.sendPacket(RequestScanPacket(20))
        return result
    }


    private fun parseLagEntity(objectData: ScanResultPacket.ObjectData): LagEntity? {
        return when (objectData.type) {
            ENTITY -> {
                val lagData = Entity(
                    objectData.data[Entry.WORLD_ID] as Int,
                    objectData.data[Entry.ENTITY_NAME] as String,
                    objectData.data[Entry.ENTITY_UUID] as UUID,
                    objectData.data[Entry.ENTITY_CLASS_NAME] as String
                )
                LagEntity(
                    objectData.data[Entry.NANOS] as Long,
                    DateType.ENTITY,
                    lagData
                )
            }
            TILE_ENTITY, BLOCK -> {
                val lagData = Block(
                    objectData.data[Entry.WORLD_ID] as Int,
                    objectData.data[Entry.BLOCK_NAME] as String,
                    objectData.data[Entry.BLOCK_POS_X] as Int,
                    objectData.data[Entry.BLOCK_POS_Y] as Int,
                    objectData.data[Entry.BLOCK_POS_Z] as Int,
                    objectData.data[Entry.BLOCK_CLASS_NAME] as String
                )
                LagEntity(
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
                    objectData.data[Entry.NANOS] as Long,
                    DateType.EVENT,
                    lagData
                )
            }
            GUI_ENTITY, GUI_BLOCK -> {
                null
            }
        }
    }
}

@Serializable
data class LagEntity(
    val nanos: Long,
    val dateType: DateType,
    val data: LagData
)

enum class DateType(id: Int) {
    ENTITY(0),
    BLOCK(1),
    EVENT(2);
}

@Serializable
sealed class LagData {
    abstract val className: String
}

@Serializable
data class Entity(
    val worldId: Int,
    val name: String,
    val uuid: @Serializable(UUIDSerializer::class) UUID,
    override val className: String
) : LagData()

@Serializable
data class Event(
    val listener: String,
    val threadType: Int,
    override val className: String
) : LagData()

@Serializable
data class Block(
    val worldId: Int,
    val name: String,
    val x: Int,
    val y: Int,
    val z: Int,
    override val className: String
) : LagData()
