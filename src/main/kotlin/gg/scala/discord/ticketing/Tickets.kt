package gg.scala.discord.ticketing

import gg.scala.discord.embed
import gg.scala.discord.role
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import java.util.*
import kotlin.random.Random

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object Tickets
{
    val tickets = mutableMapOf<TextChannel, Ticket>()

    private val encoder = Base64.getEncoder()

    fun create(event: ButtonInteractionEvent): TextChannel?
    {
        val ticketName = "ticket-${
            event.member!!.user.name
        }"

        val memberId = encoder.encodeToString(
            event.member!!.id.toByteArray()
        )

        if (
            event.message.category!!.channels
                .any {
                    it.guild.getTextChannelById(it.idLong)
                        ?.let { channel -> channel.topic == memberId } == true
                }
        ) return null

        // TODO: use .queue()
        val textChannel = event.guild!!
            .createTextChannel(
                ticketName, event.message.category
            )
            .complete()

        textChannel.manager.setTopic(memberId).queue()

        textChannel.manager
            .putPermissionOverride(
                event.member!!, 3072L, 8192L
            )
            .putPermissionOverride(
                textChannel.guild.role("@everyone")!!,
                0L, 1024L
            )
            .queue()

        val ticket = Ticket(
            event.member!!,
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

        tickets[textChannel] = ticket
        return textChannel
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
