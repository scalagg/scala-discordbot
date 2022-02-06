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
import net.dv8tion.jda.api.entities.TextChannel
import java.util.ArrayList

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class Ticket(
    val creator: Long,
    private val textChannel: TextChannel
)
{
    private val responses =
        mutableMapOf<TicketResponseStage, TicketResponse>()

    var currentStage = -1
    var completedStagePrompt = false

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
            e.printStackTrace()
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

        textChannel.history.retrievedHistory
            .forEach { it.delete().queue() }

        val creatorMember = textChannel.guild
            .getMemberById(creator)!!

        val manager = textChannel.manager
            .putPermissionOverride(
                creatorMember, 3072L, 8192L
            )
            .putPermissionOverride(
                textChannel.guild.role("@everyone")!!,
                0L, 1024L
            )

        for (supportRole in ScalaDiscordApp.settings.getSupportRoles(textChannel.guild))
        {
            manager.putPermissionOverride(
                supportRole, listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND), listOf()
            )
        }

        manager.queue()

        val embedBuilder = embed(
            "**Responses**",
            "Here are ${creatorMember.asMention}'s responses:"
        )

        for (response in responses)
        {
            embedBuilder.addField(
                response.key.stageIdentifier(),
                response.value.content, true
            )
        }

        textChannel.sendMessageEmbeds(
            embedBuilder.build()
        ).queue()
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
}
