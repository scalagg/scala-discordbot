package gg.scala.discord.ticketing.response.extended

import com.mongodb.client.model.Filters
import gg.scala.discord.embed
import gg.scala.discord.ticketing.Ticket
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.response.TicketResponseStage
import gg.scala.discord.ticketing.response.extended.models.Punishment
import gg.scala.store.controller.DataStoreObjectControllerCache
import net.dv8tion.jda.api.EmbedBuilder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object PunishmentTicketResponse : TicketResponseStage
{
    override fun stageIdentifier() = "Punishment"

    override fun acceptResponse(response: String): Boolean
    {
        return DataStoreObjectControllerCache
            .findNotNull<Punishment>()
            .mongo()
            .loadWithFilter(
                Filters.regex(
                    "_id",
                    "^${response.replace("#", "")}"
                )
            )
            .join() != null
    }

    override fun sendOptionalEmbed(response: String): EmbedBuilder?
    {
        val punishment = DataStoreObjectControllerCache
            .findNotNull<Punishment>()
            .mongo()
            .loadWithFilter(
                Filters.regex(
                    "_id",
                    "^${response.replace("#", "")}"
                )
            )
            .join()
            ?: return null

        return embed(
            "Punishment Details"
        ).apply {
            addField(
                "Type",
                punishment.category.name,
                true
            )

            addField(
                "Added",
                SimpleDateFormat().format(Date(punishment.addedAt)),
                true
            )

            addField(
                "Reason",
                punishment.addedReason,
                true
            )
        }
    }

    override fun shouldDisplay(ticket: Ticket): Boolean
    {
        return ticket.context() == TicketCreationContext.APPEAL
    }

    override fun formatTranslated(translated: String?) = translated!!
}
