package gg.scala.discord.ticketing.response

import gg.scala.discord.ticketing.Ticket
import net.dv8tion.jda.api.EmbedBuilder

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
interface TicketResponseStage
{
    fun stageIdentifier(): String

    fun acceptResponse(response: String): Boolean
    fun formatTranslated(translated: String?): String

    fun shouldDisplay(ticket: Ticket): Boolean = true

    open fun sendOptionalEmbed(response: String): EmbedBuilder?
    {
        return null
    }
}
