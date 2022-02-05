package gg.scala.discord.ticketing

import gg.scala.discord.language.Language
import gg.scala.discord.language.Languages
import gg.scala.discord.language.impl.English
import gg.scala.discord.ticketing.response.TicketResponse
import gg.scala.discord.ticketing.response.TicketResponseStage
import gg.scala.discord.ticketing.response.TicketResponseStages
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class Ticket(
    val textChannel: TextChannel
)
{
    private val responses =
        mutableMapOf<TicketResponseStage, TicketResponse>()

    fun language(): Language
    {
        return responses[TicketResponseStages.LANGUAGE]
            ?.let { Languages.find(it.content) } ?: English
    }
}
