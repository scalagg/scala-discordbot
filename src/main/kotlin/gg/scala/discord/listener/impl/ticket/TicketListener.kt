package gg.scala.discord.listener.impl.ticket

import gg.scala.discord.ticketing.Ticket
import gg.scala.discord.ticketing.Tickets
import gg.scala.discord.ticketing.response.TicketResponseStages.stages
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
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
    fun loadAllTickets() = DataStoreObjectControllerCache
        .findNotNull<Ticket>()
        .loadAll(DataStoreStorageType.REDIS)
        .join().values

    override fun onChannelDelete(event: ChannelDeleteEvent)
    {
        val ticket = this.loadAllTickets()
            .firstOrNull { it.textChannelId == event.channel.idLong }
            ?: return

        ticket.delete().join()
    }

    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent)
    {
        val ticket = this.loadAllTickets()
            .firstOrNull { it.creatorId == event.member?.id }
            ?: return

        ticket.textChannel.delete().queue()
    }

    override fun onButtonInteraction(
        event: ButtonInteractionEvent
    )
    {
        if (event.button.id == "ticket-creation")
        {
            Tickets
                .create(event)
                .thenAccept { ticket ->
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
                }
        } else if (event.button.id == "close-ticket")
        {
            val ticket = this.loadAllTickets()
                .firstOrNull {
                    it.textChannelId == event.channel.idLong
                }
                ?: return

            if (ticket.closing)
                return

            ticket.closing = true
            ticket.save().join()

            event.interaction.editButton(
                Button.danger("close-ticket", "Closing ticket... (5 seconds)")
            ).queue {
                event.channel
                    .delete()
                    .queueAfter(5L, TimeUnit.SECONDS)
            }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        val rawMessage = event.message.contentRaw

        if (event.channel.name == "counting")
        {
            try
            {
                val integer = rawMessage.toInt()

                event.channel.history.retrievePast(2)
                    .map { messages -> if (messages.isEmpty()) "" else messages[1].contentRaw }
                    .queue { message ->
                        if (message.equals(""))
                        {
                            return@queue
                        }

                        val beforeNumber = message.toInt()

                        if (beforeNumber + 1 != integer)
                        {
                            event.message.delete().queue()
                        }
                    }
            } catch (ignored: Exception)
            {
                event.message.delete().queue()
            }
            return
        }

        val ticket = this.loadAllTickets()
            .firstOrNull { it.textChannelId == event.channel.idLong }
            ?: return

        if (
            event.member == null ||
            event.member!!.id != ticket.creatorId
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

            CompletableFuture
                .supplyAsync {
                    stage.acceptResponse(content)
                }
                .exceptionally {
                    it.printStackTrace()
                    return@exceptionally false
                }
                .thenAccept { accepted ->
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

    override fun onMessageUpdate(event: MessageUpdateEvent)
    {
        if (event.channel.name == "counting")
        {
            event.message.delete().queue()
        }
    }
}
