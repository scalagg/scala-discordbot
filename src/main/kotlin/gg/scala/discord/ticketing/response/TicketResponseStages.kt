package gg.scala.discord.ticketing.response

import gg.scala.cache.uuid.ScalaStoreUuidCache
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

    @JvmStatic
    val USERNAME = object : TicketResponseStage
    {
        override fun stageIdentifier() = "Username"

        override fun acceptResponse(response: String): Boolean
        {
            // We're just using this to validate that the
            // username's an actual registered username.
            // the returned unique id will not be needed, if we do
            // need it, we can always grab it from the cache again.
            return ScalaStoreUuidCache.uniqueId(response) != null
        }

        override fun formatTranslated(translated: String?): String
        {
            return "Please enter your Minecraft IGN:"
        }
    }

    private val orderedStages =
        linkedSetOf<TicketResponseStage>()

    @Configure
    fun configure()
    {
        orderedStages.add(LANGUAGE)
        orderedStages.add(USERNAME)
    }
}
