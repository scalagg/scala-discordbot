package gg.scala.discord.listener.impl.sync

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.cache.uuid.ScalaStoreUuidCache
import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.listener.modal.ButtonActivatedModal
import gg.scala.discord.profile.DiscordProfile
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import java.util.*

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
object DiscordSyncListener : ButtonActivatedModal(id = "discordsync")
{
    private val connection = ScalaDiscordApp
        .aware.internal().connect()

    override fun modal() = Modal
        .create(
            "discordsync", "Account Link"
        )
        .addComponents(
            ActionRow.of(
                TextInput
                    .create(
                        "code",
                        "Code",
                        TextInputStyle.SHORT
                    )
                    .setPlaceholder("Your 7-character account sync code.")
                    .setMinLength(6)
                    .setMaxLength(7)
                    .build()
            )
        )
        .build()

    override fun onSubmit(event: ModalInteractionEvent)
    {
        val sync = event.getValue("code")!!.asString

        val matching = this.connection.sync()
            .get(
                "discord-sync-codes:codes:$sync"
            )

        val uniqueId = UUID.fromString(matching)

        val discordProfile = DataStoreObjectControllerCache
            .findNotNull<DiscordProfile>()
            .load(uniqueId, DataStoreStorageType.MONGO)
            .join()

        if (discordProfile == null)
        {
            event.hook.sendMessage(
                "Something went terribly wrong while trying to process your request. Please report this to an administrator. (0x1)"
            ).setEphemeral(true).queue()
            return
        }

        val privateMember = event.user

        discordProfile.discordId = privateMember.idLong
        discordProfile.lastCachedDiscordUsername = privateMember.name
        discordProfile.save().join()

        this.connection.sync().apply {
            del("discord-sync-codes:codes:$sync")
            del("discord-sync-codes:accounts:$uniqueId")
        }

        AwareMessage.of(
            "linked", ScalaDiscordApp.aware,
            "player-uuid" to uniqueId
        ).publish(
            AwareThreadContext.SYNC
        )

        event.guild!!
            .addRoleToMember(
                event.member!!,
                event.guild!!
                    .getRolesByName(
                        "✓ Account Linked", true
                    )
                    .first()
            )
            .queue()

        event.member!!
            .modifyNickname(
                ScalaStoreUuidCache.username(uniqueId)
            )
            .queue()

        event.hook.sendMessage(
            "We've successfully linked your Discord account to the Minecraft username: `${
                ScalaStoreUuidCache.username(uniqueId)
            }`!"
        ).setEphemeral(true).queue()
    }
}
