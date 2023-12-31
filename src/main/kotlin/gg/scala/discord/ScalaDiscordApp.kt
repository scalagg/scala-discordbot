package gg.scala.discord

import com.google.gson.LongSerializationPolicy
import gg.scala.aware.AwareBuilder
import gg.scala.aware.AwareHub
import gg.scala.aware.codec.codecs.interpretation.AwareMessageCodec
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.uri.WrappedAwareUri
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.distribution.DistributedRedisUuidCacheTranslator
import gg.scala.cache.uuid.resolver.impl.MojangDataResolver
import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.discord.store.ScalaDataStoreStandaloneImpl
import gg.scala.discord.ticketing.Ticket
import gg.scala.flavor.Flavor
import gg.scala.store.ScalaDataStoreShared
import gg.scala.store.controller.DataStoreObjectControllerCache
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
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
            it.setPrettyPrinting()
            it.setLongSerializationPolicy(LongSerializationPolicy.STRING)
        }

        val settingsFile = File("settings.json")

        if (!settingsFile.exists())
        {
            val newSettings = ScalaDiscordSettings()
            settingsFile.createNewFile()
            settingsFile.writeText(
                Serializers.gson.toJson(newSettings)
            )

            this.settings = newSettings
            Logger.getAnonymousLogger().info("A new settings object has been created in ${settingsFile.absolutePath}.")
        } else
        {
            settings = Serializers.gson.fromJson(
                File("settings.json")
                    .bufferedReader(),
                ScalaDiscordSettings::class.java
            )
        }

        if (settings.token.isBlank())
        {
            Logger.getAnonymousLogger().info("Your token is empty! Stopping process.")
            return
        }

        client = JDABuilder
            .createDefault(settings.token)
            .setEnabledIntents(
                GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
            )
            .setMemberCachePolicy(MemberCachePolicy.ALL)
            .build()

        AwareHub.configure(
            WrappedAwareUri(address = settings.redisHost, port = settings.redisPort)
        ) {
            Serializers.gson
        }

        ScalaDataStoreShared.INSTANCE =
            ScalaDataStoreStandaloneImpl

        DataStoreObjectControllerCache.create<Ticket>()

        ScalaStoreUuidCache.configure(
            DistributedRedisUuidCacheTranslator(aware),
            MojangDataResolver
        )

        flavor.bind<JDA>() to client
        flavor.bind<ScalaDiscordSettings>() to settings

        flavor.startup()

        client.awaitReady()
    }

    fun stop()
    {
        flavor.close()
        client.shutdownNow()
    }
}
