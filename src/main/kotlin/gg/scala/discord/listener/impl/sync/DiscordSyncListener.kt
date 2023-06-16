package gg.scala.discord.listener.impl.sync

import com.google.common.cache.CacheBuilder
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import gg.scala.discord.profile.DiscordProfile
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
object DiscordSyncListener : ListenerAdapter()
{
    private val pendingSync = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10L))
        .build<Long, Long>()

    private val connection = ScalaDiscordApp
        .aware.internal().connect()

    override fun onMessageReceived(
        event: MessageReceivedEvent
    )
    {
        if (event.channelType != ChannelType.PRIVATE)
        {
            return
        }

        if (event.message.author.isBot)
            return

        val guildId = ScalaDiscordApp
            .settings.guild

        val guild = event.jda
            .getGuildById(guildId)
            ?: return

        if (this.pendingSync.asMap().containsKey(event.author.idLong))
        {
            event.message
                .reply("Verifying your account...")
                .queue { message ->
                    val matching = this.connection.sync()
                        .get(
                            "discord-sync-codes:codes:${event.message.contentRaw}"
                        )

                    if (matching == null)
                    {
                        message.editMessage(
                            "We were unable to verify your account! Are you sure you enter the right code?"
                        ).queue()
                        return@queue
                    }

                    val uniqueId = UUID.fromString(matching)

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

                    val privateMember = event.author

                    discordProfile.discordId = privateMember.idLong
                    discordProfile.lastCachedDiscordUsername = privateMember.name
                    discordProfile.save().join()

                    this.connection.sync().apply {
                        del("discord-sync-codes:codes:${event.message.contentRaw}")
                        del("discord-sync-codes:accounts:$uniqueId")
                    }

                    AwareMessage.of(
                        "linked", ScalaDiscordApp.aware,
                        "player-uuid" to uniqueId
                    ).publish(
                        AwareThreadContext.SYNC
                    )

                    guild
                        .addRoleToMember(
                            event.member!!,
                            guild
                                .getRolesByName(
                                    "✓ Account Linked", true
                                )
                                .first()
                        )
                        .queue()

                    message.editMessage(
                        "We've successfully linked your Discord account to the Minecraft username: `${
                            ScalaStoreUuidCache.username(uniqueId)
                        }`!"
                    ).queue {
                        this.pendingSync.invalidate(privateMember.idLong)
                    }
                }
        }
    }

    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "discord-syncing" && event.member != null)
        {
            if (this.pendingSync.asMap().containsKey(event.member!!.idLong))
            {
                event.interaction
                    .reply("We already sent you a message. Check your DMs!")
                    .setEphemeral(true)
                    .queue()
                return
            }

            event.member!!.user
                .openPrivateChannel()
                .queue { channel ->
                    channel
                        .sendMessageEmbeds(
                            embed(
                                "Enter your code",
                                "Enter the 7 character code you received in-game to completed the Minecraft verification process!"
                            ).build()
                        )
                        .onErrorMap {
                            event.interaction
                                .reply("We were unable to send you a message. Are you sure you are allowing private messages from me?")
                                .setEphemeral(true)
                                .queue()
                            return@onErrorMap null
                        }
                        .queue contextual@{
                            if (it == null)
                            {
                                return@contextual
                            }

                            this.pendingSync.put(
                                event.member!!.idLong,
                                event.member!!.idLong
                            )

                            event.interaction
                                .reply("We sent you a message, check your DMs!")
                                .setEphemeral(true)
                                .queue()
                        }
                }
        }
    }
}
