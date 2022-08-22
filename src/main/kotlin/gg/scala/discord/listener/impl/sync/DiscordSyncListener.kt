package gg.scala.discord.listener.impl.sync

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import gg.scala.discord.profile.DiscordProfile
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
object DiscordSyncListener : ListenerAdapter()
{
    private val pendingSync = mutableListOf<Long>()
    private val aware = ScalaDiscordApp.aware.internal().connect()

    override fun onMessageReceived(
        event: MessageReceivedEvent
    )
    {
        try
        {
            event.privateChannel
        } catch (ignored: Exception)
        {
            return
        }

        if (event.message.author.isBot)
            return

        if (event.privateChannel.user.idLong in this.pendingSync)
        {
            event.channel.sendMessage(
                "Verifying your account..."
            ).queue { message ->
                val matching = this.aware.sync()
                    .keys("discord-sync-codes:*")
                    .mapNotNull {
                        val value = this.aware
                            .sync().get(it)
                            ?: return@mapNotNull null

                        it to value
                    }
                    .find {
                        it.second.equals(
                            event.message.contentRaw, true
                        )
                    }

                if (matching == null)
                {
                    message.editMessage(
                        "We were unable to verify your account! Are you sure you enter the right code?"
                    ).queue()
                    return@queue
                }

                val uniqueId = UUID.fromString(
                    matching.first
                        .replace(
                            "discord-sync-codes:", ""
                        )
                )

                val discordProfile = DataStoreObjectControllerCache
                    .findNotNull<DiscordProfile>()
                    .load(uniqueId, DataStoreStorageType.MONGO)
                    .join()

                if (discordProfile == null)
                {
                    message.editMessage(
                        "Something went terribly wrong while trying to process your request. Please report this to an administrator. (0x1)"
                    ).queue()
                    return@queue
                }

                discordProfile.discordId = event.member!!.idLong
                discordProfile.lastCachedDiscordUsername =
                    "${event.member!!.user.name}${event.member!!.user.discriminator}"
                discordProfile.save().join()

                message.editMessage(
                    "We've successfully linked your Discord account to the Minecraft username: `${
                        ScalaStoreUuidCache.username(uniqueId)
                    }`!"
                ).queue()
            }
        }
    }

    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "discord-syncing" && event.member != null)
        {
            if (this.pendingSync.contains(event.member!!.idLong))
            {
                event.interaction.reply("We already sent you a message. Check your DMs!")
                    .setEphemeral(true)
                    .queue()
                return
            }

            event.member!!.user
                .openPrivateChannel()
                .onErrorMap {
                    event.interaction.reply("We were unable to send you a message. Are you sure you are allowing private messages from me?")
                        .setEphemeral(true)
                        .queue()
                    return@onErrorMap null
                }
                .queue { channel ->
                    if (channel != null)
                    {
                        this.pendingSync.add(event.member!!.idLong)

                        channel.sendMessageEmbeds(
                            embed(
                                "Enter your code",
                                "Enter the 7 digit code you received in-game to completed the Minecraft verification process!"
                            ).build()
                        ).queue {
                            event.interaction.reply("We sent you a message, check your DMs!")
                                .setEphemeral(true)
                                .queue()
                        }
                    }
                }
        }
    }
}
