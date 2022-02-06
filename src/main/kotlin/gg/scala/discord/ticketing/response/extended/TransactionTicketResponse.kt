package gg.scala.discord.ticketing.response.extended

import gg.scala.discord.ticketing.Ticket
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.response.TicketResponseStage

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object TransactionTicketResponse : TicketResponseStage
{
    override fun stageIdentifier() = "Transaction"

    override fun acceptResponse(response: String): Boolean
    {
        // TODO: 2/5/2022 handle logic through like, Tebex api?
        return true
    }

    override fun shouldDisplay(ticket: Ticket): Boolean
    {
        return ticket.context() == TicketCreationContext.APPEAL
    }

    override fun formatTranslated(translated: String?) = translated!!
}
