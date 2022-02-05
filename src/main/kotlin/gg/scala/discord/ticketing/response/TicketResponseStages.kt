package gg.scala.discord.ticketing.response

import gg.scala.discord.language.Languages
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

    private val orderedStages =
        linkedSetOf<TicketResponseStage>()

    @Configure
    fun configure()
    {
        orderedStages.add(LANGUAGE)
    }
}
