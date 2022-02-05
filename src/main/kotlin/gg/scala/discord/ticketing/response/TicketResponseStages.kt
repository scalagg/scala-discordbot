package gg.scala.discord.ticketing.response

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
    val LANGUAGE = object : TicketResponseStage {
        override fun stageIdentifier() = "Language"

        override fun acceptResponse(response: String): Boolean
        {
            TODO("Not yet implemented")
        }
    }

    private val orderedStages =
        linkedSetOf<TicketResponseStage>()

    @Configure
    fun configure()
    {
//        orderedStages.add()
    }
}
