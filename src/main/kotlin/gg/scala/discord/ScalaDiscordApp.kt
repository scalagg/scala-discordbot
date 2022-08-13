package gg.scala.discord

import com.google.gson.LongSerializationPolicy
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.JsonRedisCodec
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import gg.scala.cache.uuid.impl.distribution.DistributedRedisUuidCacheTranslator
import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.discord.store.ScalaDataStoreStandaloneImpl
import gg.scala.discord.ticketing.Ticket
import gg.scala.flavor.Flavor
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.controller.DataStoreObjectControllerCache
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.evilblock.cubed.serializers.Serializers
import java.io.File
import java.util.logging.Logger

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ScalaDiscordApp
{
    lateinit var settings: ScalaDiscordSettings

    lateinit var client: JDA

    private val flavor = Flavor
        .create<ScalaDiscordApp>()

    val aware by lazy {
        AwareBuilder
            .of<AwareMessage>("discord")
            .codec(AwareMessageCodec)
            .logger(Logger.getAnonymousLogger())
            .build()
    }

    fun start()
    {
        Serializers.useGsonBuilderThenRebuild {
            it.setLongSerializationPolicy(LongSerializationPolicy.STRING)
        }

        settings = Serializers.gson.fromJson(
            File("settings.json")
                .bufferedReader(),
            ScalaDiscordSettings::class.java
        )

        client = JDABuilder
            .createDefault(settings.token)
            .enableIntents(
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
            )
            .build()

        client.awaitReady()

        AwareHub.configure(
            WrappedAwareUri(address = settings.redisHost)
        ) {
            Serializers.gson
        }

        ScalaDataStoreShared.INSTANCE =
            ScalaDataStoreStandaloneImpl

        DataStoreObjectControllerCache.create<Ticket>()

        ScalaStoreUuidCache.configure(
            DistributedRedisUuidCacheTranslator(aware)
        )

        flavor.bind<JDA>() to client
        flavor.bind<ScalaDiscordSettings>() to settings

        flavor.startup()
    }

    fun stop()
    {
        flavor.close()
        client.shutdownNow()
    }
}
