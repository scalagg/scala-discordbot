package gg.scala.discord.ticketing.response

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.discord.language.Languages
import gg.scala.discord.ticketing.Ticket
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.context.TicketCreationContexts
//import gg.scala.discord.ticketing.response.extended.PunishmentTicketResponse
//import gg.scala.discord.ticketing.response.extended.TransactionTicketResponse
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object TicketResponseStages
{
    @JvmStatic
    val LANGUAGE = object : TicketResponseStage
    {
        override fun stageIdentifier() = "Language"

        override fun acceptResponse(response: String): Boolean
        {
            return Languages.find(response) != null
        }

        override fun formatTranslated(translated: String?): String
        {
            return "Please choose a language below:\n`${
                Languages.languages.joinToString { it.getId() }
            }`"
        }
    }

    @JvmStatic
    val USERNAME = object : TicketResponseStage
    {
        override fun stageIdentifier() = "Username"
        override fun acceptResponse(response: String): Boolean
        {
            return ScalaStoreUuidCache.uniqueId(response) != null
        }

        override fun formatTranslated(translated: String?) = translated!!
    }

    @JvmStatic
    val CONTEXT = object : TicketResponseStage
    {
        // Just going to use category to not
        // confuse possible ticket creators
        override fun stageIdentifier() = "Category"

        override fun acceptResponse(response: String): Boolean
        {
            return TicketCreationContexts.parse(response) != null
        }

        override fun formatTranslated(translated: String?): String
        {
            return String.format(
                translated!!, TicketCreationContext.values()
                .joinToString { it.formattedDisplay }
            )
        }
    }

    @JvmStatic
    val REASONING = object : TicketResponseStage
    {
        override fun stageIdentifier() = "Reason"
        override fun acceptResponse(response: String) = true

        override fun formatTranslated(translated: String?) = translated!!
    }

    val orderedStages =
        linkedSetOf<TicketResponseStage>()

    private fun getOrderedStagesWithinContext(ticket: Ticket) =
        orderedStages.filter { it.shouldDisplay(ticket) }

    fun Ticket.stages() = getOrderedStagesWithinContext(this)

    @Configure
    fun configure()
    {
        orderedStages.add(LANGUAGE)
        orderedStages.add(USERNAME)
        orderedStages.add(CONTEXT)
        orderedStages.add(REASONING)
        // optional, extended responses
//        orderedStages.add(PunishmentTicketResponse)
//        orderedStages.add(TransactionTicketResponse)
    }
}
