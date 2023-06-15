package gg.scala.discord.ticketing

import gg.scala.discord.embed
import gg.scala.discord.listener.impl.ticket.TicketListener
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object Tickets
{
    fun create(event: ButtonInteractionEvent): CompletableFuture<TextChannel?>
    {
        val ticketName = "ticket-${
            event.member!!.user.name
        }"

        val existing = TicketListener.loadAllTickets()
            .any {
                it.creatorId == event.member!!.id
            }

        if (existing)
        {
            return CompletableFuture.completedFuture(null)
        }

        val future = CompletableFuture<TextChannel?>()

        event.guild!!
            .createTextChannel(
                ticketName, event.message.category
            )
            .queue { textChannel ->
                if (textChannel == null)
                {
                    future.complete(null)
                    return@queue
                }

                textChannel.manager
                    .putPermissionOverride(
                        event.member!!,
                        listOf(
                            Permission.VIEW_CHANNEL,
                            Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_EMBED_LINKS,
                            Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_ADD_REACTION,
                            Permission.MESSAGE_EXT_EMOJI
                        ),
                        listOf()
                    )
                    .reason("Add Ticket-Author to ticket")
                    .queue()

                val ticket = Ticket(
                    UUID.randomUUID(),
                    event.user.id,
                    textChannel.idLong,
                    textChannel.guild.idLong
                )

                textChannel.sendMessageEmbeds(
                    embed(
                        "Support Ticket",
                        "Thanks for contacting our support team! Please answer the questions provided below until one of our support members are available.",
                        "", "Opened by: ${event.member!!.asMention}"
                    ).build()
                ).queue {
                    ticket.startNextStage()
                    textChannel.sendMessage(event.member!!.user.asMention).queue()
                }

                ticket.save().join()
                future.complete(textChannel)
            }

        return future
    }

    private const val SALT = "abcdefghijklmnopqrstuvwxyz1234567890"

    fun generateRandomString(): String
    {
        val saltedString = StringBuilder()

        while (saltedString.length < 8)
        {
            val index = (Random.nextFloat() * SALT.length.toFloat()).toInt()
            saltedString.append(SALT[index])
        }

        return saltedString.toString()
    }
}
