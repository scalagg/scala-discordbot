package gg.scala.discord.listener.impl.ticket

import gg.scala.discord.ticketing.Tickets
import gg.scala.discord.ticketing.response.TicketResponseStages.stages
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object TicketListener : ListenerAdapter()
{
    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "ticket-creation")
            Tickets.create(event)
    }

    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        val ticket = Tickets
            .tickets[event.textChannel]
            ?: return

        if (
            event.member == null ||
            event.member!!.idLong != ticket.creator
        ) return

        if (!ticket.completedStagePrompt)
        {
            if (ticket.currentStage == -1)
            {
                event.message.delete().queue()
                return
            }

            val content = event.message
                .contentStripped

            val stage = ticket
                .stages()[ticket.currentStage]

            CompletableFuture.supplyAsync {
                stage.acceptResponse(content)
            }.thenAccept { accepted ->
                if (accepted)
                {
                    ticket.startNextStage()
                } else
                {
                    if (ticket.currentStage == 0)
                    {
                        event.message.reply(
                            "That's an invalid language, fallbacking to `English`."
                        ).queue()
                        return@thenAccept
                    }

                    event.message.reply(ticket.invalid()).queue()

                    ticket.sendStageMessage(stage)
                }
            }
        }
    }
}
