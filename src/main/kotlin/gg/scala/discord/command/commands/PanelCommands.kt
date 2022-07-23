package gg.scala.discord.command.commands

import gg.scala.discord.embed
import net.dv8tion.jda.api.interactions.components.buttons.Button
import revxrsal.commands.annotation.Command
import revxrsal.commands.jda.annotation.RolePermission
import revxrsal.commands.jda.core.BaseActorJDA

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object PanelCommands
{
    @Command("verification-panel")
    @RolePermission(
        names = ["Developer"]
    )
    fun onVerifyPanel(actor: BaseActorJDA)
    {
        val channel = actor.guild
            .getTextChannelsByName("create-a-ticket", true)
            .firstOrNull()

        val embed = embed(
            "\uD83D\uDEE1️ **Verification**",
            "Interact with the button below to verify yourself!",
            "",
            "**INFO:**",
            " - Not receiving a message? Make sure you have private messages enabled!",
            " - Have any other questions? Create a ticket in <#${channel?.id}>!"
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "verification",
                    "Start verification process"
                )
            )
            .queue()
    }

    @Command("ticket-panel")
    @RolePermission(
        names = ["Developer"]
    )
    fun onTicketPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "\uD83C\uDF9F **Support**",
            "Interact with the button below to create a support ticket!",
            "",
            "**INFO:**",
            " - Please wait patiently as one of our staff members respond.",
            " - Please respond to all questions (staff members will not respond to the ticket unless they are complete)!"
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "ticket-creation",
                    "Create a ticket"
                )
            )
            .queue()
    }

    @Command("link-panel")
    @RolePermission(
        names = ["Developer"]
    )
    fun onSyncPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "\uD83D\uDD17 **Discord Link**",
            "Interact with the button below to link your discord account!",
            "",
            "**INFO:**",
            " - Our system may be slow at times, please contact an administrator if an issue occurs."
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "discord-syncing", "Link your account"
                )
            )
            .queue()
    }
}
