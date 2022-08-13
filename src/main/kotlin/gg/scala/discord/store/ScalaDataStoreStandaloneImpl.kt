package gg.scala.discord.store

import gg.scala.discord.ScalaDiscordApp
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.connection.AbstractDataStoreConnection
import gg.scala.store.connection.mongo.AbstractDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.UriDataStoreMongoConnection
import gg.scala.store.connection.mongo.impl.details.DataStoreMongoConnectionDetails
import gg.scala.store.connection.redis.AbstractDataStoreRedisConnection

/**
 * @author GrowlyX
 * @since 8/13/2022
 */
object ScalaDataStoreStandaloneImpl : ScalaDataStoreShared()
{
    override fun getNewRedisConnection(): AbstractDataStoreRedisConnection
    {
        return object : AbstractDataStoreRedisConnection()
        {
            override fun createNewConnection() = ScalaDiscordApp.aware
        }
    }

    override fun getNewMongoConnection(): AbstractDataStoreMongoConnection
    {
        return UriDataStoreMongoConnection(
            DataStoreMongoConnectionDetails()
        )
    }

    override fun debug(from: String, message: String)
    {
        AbstractDataStoreConnection.LOGGER.info("$from: $message")
    }
}
