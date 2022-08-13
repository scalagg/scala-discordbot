package gg.scala.discord.ticketing

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import gg.scala.discord.language.Language
import gg.scala.discord.language.Languages
import gg.scala.discord.language.impl.English
import gg.scala.discord.role
import gg.scala.discord.settings.ScalaDiscordSettings
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.context.TicketCreationContexts
import gg.scala.discord.ticketing.response.TicketResponse
import gg.scala.discord.ticketing.response.TicketResponseStage
import gg.scala.discord.ticketing.response.TicketResponseStages
import gg.scala.discord.ticketing.response.TicketResponseStages.stages
import gg.scala.flavor.inject.Inject
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.util.ArrayList

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class Ticket(
    val creator: Member,
    private val textChannelId: Long,
    private val guildId: Long,
)
{
    val textChannel: TextChannel
        get() = ScalaDiscordApp.client
            .getGuildById(guildId)!!
            .getTextChannelById(textChannelId)!!

    private val responses =
        mutableMapOf<TicketResponseStage, TicketResponse>()

    var currentStage = -1
    var completedStagePrompt = false

    var closing = false

    fun startNextStage()
    {
        val stagesWithinContext = stages()

        try
        {
            val nextStage =
                stagesWithinContext[++currentStage]

            sendStageMessage(nextStage)
        } catch (e: Exception)
        {
            completeStagePrompt()
        }
    }

    fun sendStageMessage(
        stage: TicketResponseStage
    )
    {
        val language = language()
        val identifier = stage
            .stageIdentifier().lowercase()

        val localeMessage = language
            .locale[identifier]

        textChannel.sendMessage(
            stage.formatTranslated(localeMessage)
        ).queue()
    }

    private fun completeStagePrompt()
    {
        completedStagePrompt = true

        textChannel.history
            .retrievePast(99)
            .queue {
                it.forEach { message ->
                    message.delete().queue()
                }

                val manager = textChannel.manager

                for (supportRole in ScalaDiscordApp.settings.getSupportRoles(textChannel.guild))
                {
                    manager.putPermissionOverride(
                        supportRole, listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), listOf()
                    )
                }

                manager.queue()

                val embedBuilder = embed(
                    "**Responses**",
                    "Here are ${creator.asMention}'s responses:"
                )

                for (response in responses)
                {
                    embedBuilder.addField(
                        response.key.stageIdentifier(),
                        response.value.content, true
                    )
                }

                textChannel.sendMessageEmbeds(embedBuilder.build())
                    .setActionRow(
                        Button.danger("close-ticket", "Click to close")
                    )
                    .queue { message ->
                        message.pin().queue {
                            textChannel.sendMessage("${creator.asMention}, please wait patiently as a staff member responds to this ticket.").queue()
                        }
                    }
            }
    }

    fun invalid(): String = language()
        .getTranslation("invalid")!!

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

    fun addStageResponse(stage: TicketResponseStage, content: String)
    {
        responses[stage] = TicketResponse(content)
    }
}
