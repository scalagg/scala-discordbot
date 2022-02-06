package gg.scala.discord.ticketing

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import gg.scala.flavor.service.Service
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import kotlin.random.Random

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object Tickets
{
    val tickets = mutableMapOf<TextChannel, Ticket>()

    fun create(event: ButtonInteractionEvent)
    {
        val ticketName = "ticket-${
            event.member!!.user.name
        }-${
            generateRandomString()
        }"

        if (
            event.message.category!!.channels
                .firstOrNull {
                    it.name.contains(event.member!!.user.name.lowercase())
                } != null
        ) return

        val textChannel = event.guild!!
            .createTextChannel(
                ticketName, event.message.category
            )
            .complete()

        val ticket = Ticket(
            event.member!!.idLong, textChannel
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
    }

    private const val SALT = "abcdefghijklmnopqrstuvwxyz1234567890"

    private fun generateRandomString(): String
    {
        val saltedString = StringBuilder()

        while (saltedString.length < 5)
        {
            val index = (Random.nextFloat() * SALT.length.toFloat()).toInt()
            saltedString.append(SALT[index])
        }

        return saltedString.toString()
    }
}
