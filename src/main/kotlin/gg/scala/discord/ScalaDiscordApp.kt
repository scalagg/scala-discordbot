package gg.scala.discord

import com.google.gson.LongSerializationPolicy
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.cache.uuid.impl.LocalUuidCacheTranslator
import gg.scala.discord.activity.ScalaDiscordActivityRotator
import gg.scala.discord.command.ScalaDiscordCommandService
import gg.scala.discord.language.Languages
import gg.scala.discord.listener.ScalaDiscordListenerService
import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.discord.ticketing.Tickets
import gg.scala.discord.ticketing.response.TicketResponseStages
import gg.scala.flavor.Flavor
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.evilblock.cubed.serializers.Serializers
import java.io.File

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ScalaDiscordApp
{
    lateinit var settings: ScalaDiscordSettings

    private lateinit var client: JDA

    private val flavor = Flavor
        .create<ScalaDiscordApp>()

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

        ScalaStoreUuidCache.configure(
            LocalUuidCacheTranslator
        )

        flavor.bind<JDA>() to client
        flavor.bind<ScalaDiscordSettings>() to settings

        flavor.inject(Tickets)
        flavor.inject(Languages)

        flavor.inject(ScalaDiscordCommandService)
        flavor.inject(ScalaDiscordListenerService)
        flavor.inject(ScalaDiscordActivityRotator)

        flavor.inject(TicketResponseStages)
        // uncomment for prod
        // flavor.startup()
    }

    fun stop()
    {
        flavor.close()
        client.shutdownNow()
    }
}
