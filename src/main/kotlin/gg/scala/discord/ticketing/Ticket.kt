package gg.scala.discord.ticketing

import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.embed
import gg.scala.discord.language.Language
import gg.scala.discord.language.Languages
import gg.scala.discord.language.impl.English
import gg.scala.discord.listener.ScalaDiscordListenerService
import gg.scala.discord.ticketing.context.TicketCreationContext
import gg.scala.discord.ticketing.context.TicketCreationContexts
import gg.scala.discord.ticketing.response.TicketResponse
import gg.scala.discord.ticketing.response.TicketResponseStage
import gg.scala.discord.ticketing.response.TicketResponseStages.orderedStages
import gg.scala.discord.ticketing.response.TicketResponseStages.stages
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class Ticket(
    override val identifier: UUID,
    val creatorId: String,
    val textChannelId: Long,
    private val guildId: Long
) : IDataStoreObject
{
    val textChannel: TextChannel
        get() = ScalaDiscordApp.client
            .getGuildById(guildId)!!
            .getTextChannelById(textChannelId)!!

    fun get(stageId: String) = orderedStages
        .first {
            it.stageIdentifier() == stageId
        }

    private val responses =
        mutableMapOf<String, TicketResponse>()

    fun findResponse(id: String) =
        this.responses.entries
            .firstOrNull {
                it.key == id
            }
            ?.value

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
            save().join()
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
        save().join()

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

                val member = ScalaDiscordListenerService.mappings[this.creatorId]
                    ?: return@queue

                val embedBuilder = embed(
                    "**Responses**",
                    "Here are ${member.asMention}'s responses:"
                )

                embedBuilder.setThumbnail(
                    "https://crafatar.com/renders/body/${
                        ScalaStoreUuidCache.uniqueId(this.responses["Username"]!!.content)
                    }?overlay"
                )

                for (response in responses)
                {
                    embedBuilder.addField(
                        get(response.key).stageIdentifier(),
                        response.value.content, true
                    )
                }

                textChannel.sendMessageEmbeds(embedBuilder.build())
                    .setActionRow(
                        Button.danger("close-ticket", "Click to close")
                    )
                    .queue { message ->
                        message.pin().queue {
                            textChannel.sendMessage("${member.asMention}, please wait patiently as a staff member responds to this ticket.").queue()
                        }
                    }
            }
    }

    fun invalid(): String = language()
        .getTranslation("invalid")!!

    fun language(): Language
    {
        return this.findResponse("Language")
            ?.let { Languages.find(it.content) } ?: English
    }

    fun context(): TicketCreationContext
    {
        return this.findResponse("Category")
            ?.let { TicketCreationContexts.parse(it.content) }
            ?: TicketCreationContext.OTHER
    }

    fun addStageResponse(stage: TicketResponseStage, content: String)
    {
        this.responses[stage.stageIdentifier()] = TicketResponse(content)
        this.save().join()
    }

    fun delete(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Ticket>()
            .delete(this.identifier, DataStoreStorageType.REDIS)
    }

    fun save(): CompletableFuture<Void>
    {
        return DataStoreObjectControllerCache
            .findNotNull<Ticket>()
            .save(this, DataStoreStorageType.REDIS)
    }
}
