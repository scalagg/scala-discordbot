package gg.scala.discord.ticketing.response.extended.models

import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*
import java.util.concurrent.CompletableFuture

class Punishment(
    val uuid: UUID,
    val target: UUID,
    addedAt: Long,
    duration: Long,
    val addedReason: String,
    val category: PunishmentCategory
): LemonExpirable(addedAt, duration), Savable, IDataStoreObject
{
    override val identifier: UUID
        get() = uuid

    var removedAt: Long = -1

    val isRemoved: Boolean
        get() = removedAt != -1L

    val isActive: Boolean
        get() = !isRemoved && !hasExpired

    override fun save(): CompletableFuture<Void> {
        return DataStoreObjectControllerCache.findNotNull<Punishment>()
            .save(this, DataStoreStorageType.MONGO)
    }
}
