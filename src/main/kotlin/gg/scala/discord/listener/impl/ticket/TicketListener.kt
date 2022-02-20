package gg.scala.discord.listener.impl.ticket

import gg.scala.discord.ticketing.Tickets
import gg.scala.discord.ticketing.response.TicketResponseStages.stages
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object TicketListener : ListenerAdapter()
{
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent)
    {
        val ticket = Tickets.tickets.values
            .firstOrNull { it.creator == event.member }
            ?: return

        ticket.textChannel.delete().queue()
        Tickets.tickets.remove(ticket.textChannel)
    }

    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "ticket-creation")
        {
            val ticket = Tickets.create(event)

            if (ticket != null)
            {
                event.interaction.reply("Your ticket was created, view it here: ${ticket.asMention}.")
                    .setEphemeral(true)
                    .queue()
            } else
            {
                event.interaction.reply("You already have an open ticket.")
                    .setEphemeral(true)
                    .queue()
            }
        } else if (event.button.id == "close-ticket")
        {
            val ticket = Tickets
                .tickets[event.textChannel]
                ?: return

            if (ticket.closing)
                return

            ticket.closing = true

            event.interaction.editButton(
                Button.danger("close-ticket", "Closing ticket... (5 seconds)")
            ).queue {
                event.textChannel.delete()
                    .queueAfter(5L, TimeUnit.SECONDS) {
                        Tickets.tickets.remove(event.textChannel)
                    }
            }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        try
        {
            event.textChannel
        } catch (ignored: Exception)
        {
            return
        }

        val ticket = Tickets
            .tickets[event.textChannel]
            ?: return

        if (
            event.member == null ||
            event.member!!.idLong != ticket.creator.idLong
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
                    ticket.addStageResponse(
                        stage, content
                    )
                    ticket.startNextStage()
                } else
                {
                    if (ticket.currentStage == 0)
                    {
                        event.message.reply(
                            "That's an invalid language, fallbacking to `English`."
                        ).queue()

                        ticket.startNextStage()
                        return@thenAccept
                    }

                    event.message.reply(ticket.invalid()).queue()

                    ticket.sendStageMessage(stage)
                }
            }
        }
    }
}
