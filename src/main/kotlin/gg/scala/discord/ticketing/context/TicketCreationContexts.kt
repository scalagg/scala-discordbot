package gg.scala.discord.ticketing.context

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object TicketCreationContexts
{
    fun parse(raw: String): TicketCreationContext?
    {
        return TicketCreationContext.values().firstOrNull {
            it.formattedDisplay.equals(raw, true)
        }
    }
}
