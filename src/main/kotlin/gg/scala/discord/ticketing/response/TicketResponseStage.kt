package gg.scala.discord.ticketing.response

import gg.scala.discord.ticketing.Ticket

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
}
