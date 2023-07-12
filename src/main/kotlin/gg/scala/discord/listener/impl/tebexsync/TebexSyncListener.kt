package gg.scala.discord.listener.impl.tebexsync

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import kong.unirest.JsonNode
import kong.unirest.Unirest
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
object TebexSyncListener : ListenerAdapter()
{
    private val tebexSync = mutableListOf<String>()

    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "tebexsync")
        {
            val betaWhitelist = event.guild!!
                .getRolesByName("Early Access", true)
                .first()

            if (event.member!!.roles.any { it.id == betaWhitelist.id })
            {
                event
                    .reply("You're already part of the Early Access program. There is no need for you to confirm your transaction.")
                    .setEphemeral(true)
                    .queue()
                return
            }

            if (event.member!!.id in tebexSync)
            {
                event
                    .reply("You're already in the process of receiving your Early Access role. Make sure you are allowing direct messages from our Discord bot and try again.")
                    .setEphemeral(true)
                    .queue()

                tebexSync.remove(event.member!!.id)
                return
            }

            val privateChannel = try
            {
                event.member!!.user.openPrivateChannel()
                    .complete()
            } catch (ignored: Exception)
            {
                event.reply("We were unable to send you a private message! Are you allowing private messages?")
                    .setEphemeral(true)
                    .queue()
                return
            }

            val embed = embed(
                "Early Access Program",
                "Enter your Tropic store transaction ID to join our Early Access program."
            )

            try
            {
                privateChannel.sendMessageEmbeds(embed.build()).queue()
            } catch (ignored: Exception)
            {
                event.reply("We were unable to send you a private message! Are you allowing private messages?")
                    .setEphemeral(true).queue()
                return
            }

            event.reply("We sent you a message, check your private messages!")
                .setEphemeral(true).queue()
            tebexSync.add(event.member!!.id)
        }
    }

    override fun onGuildMemberRemove(
        event: GuildMemberRemoveEvent
    )
    {
        if (event.member == null)
            return

        this.tebexSync.remove(event.member!!.id)
    }

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

        println(tebexSync)

        if (tebexSync.none { it == event.author.id })
        {
            return
        }

        event.message
            .reply("Processing your transaction, please wait...")
            .queue {
                val betaWhitelist = guild
                    .getRolesByName("Early Access", true)
                    .first()

                data class Package(val id: Int, val name: String)

                data class Response(
                    val status: String,
                    val packages: List<Package>
                )

                val response = Serializers
                    .gson
                    .fromJson(
                        Unirest.get("https://plugin.tebex.io/payments/${event.message.contentStripped}")
                            .header("accept", "application/json")
                            .header("X-Tebex-Secret", ScalaDiscordApp.settings.tebexApiKey)
                            .asString()
                            .body,
                        Response::class.java
                    )

                if (response.status != "Complete")
                {
                    tebexSync.remove(event.author.id)
                    it
                        .editMessage(
                            "We have received your purchase, however, it is still being processed. Please try again later!"
                        )
                        .queue()
                    return@queue
                }

                if (response.packages.none { pkg -> (pkg.name == "Basic Bundle" || pkg.name == "Premium Bundle") })
                {
                    tebexSync.remove(event.author.id)
                    it
                        .editMessage(
                            "Sorry, but your purchase does not include an invitation to the Early Access program."
                        )
                        .queue()
                    return@queue
                }

                it
                    .editMessage(
                        "**Your purchase contains an Early Access program invitation.** You should be able to log on the server using the address `tropic.gg`. You will be receiving the `Early Access` role in our Discord server shortly."
                    )
                    .queue()

                guild
                    .addRoleToMember(
                        event.author,
                        betaWhitelist
                    )
                    .queue()
            }
    }
}
