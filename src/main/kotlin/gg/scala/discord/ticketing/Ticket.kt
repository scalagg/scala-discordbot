package gg.scala.discord.ticketing

import gg.scala.discord.language.Language
import gg.scala.discord.language.Languages
import gg.scala.discord.language.impl.English
import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.context.TicketCreationContexts
import gg.scala.discord.ticketing.response.TicketResponse
import gg.scala.discord.ticketing.response.TicketResponseStage
import gg.scala.discord.ticketing.response.TicketResponseStages
import gg.scala.flavor.inject.Inject
import net.dv8tion.jda.api.entities.TextChannel

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class Ticket(
    val creator: Long, val textChannel: TextChannel
)
{
    private val responses =
        mutableMapOf<TicketResponseStage, TicketResponse>()

    @Inject
    lateinit var settings: ScalaDiscordSettings

    fun language(): Language
    {
        return responses[TicketResponseStages.LANGUAGE]
            ?.let { Languages.find(it.content) } ?: English
    }

    fun context(): TicketCreationContext
    {
        return responses[TicketResponseStages.CONTEXT]
            ?.let { TicketCreationContexts.parse(it.content) }
            ?: TicketCreationContext.OTHER
    }

    fun username(): String
    {
        return responses[TicketResponseStages.USERNAME]?.content!!
    }
}
