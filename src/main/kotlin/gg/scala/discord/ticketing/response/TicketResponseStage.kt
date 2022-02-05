package gg.scala.discord.ticketing.response

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
interface TicketResponseStage
{
    fun stageIdentifier(): String
    fun acceptResponse(response: String): Boolean
}
