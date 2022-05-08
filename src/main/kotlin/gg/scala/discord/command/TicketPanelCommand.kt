package gg.scala.discord.command

import gg.scala.discord.embed
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.Button
import revxrsal.commands.annotation.Command
import revxrsal.commands.jda.annotation.RolePermission
import revxrsal.commands.jda.core.BaseActorJDA

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object TicketPanelCommand
{
    @Command("verification-panel")
    @RolePermission(
        names = ["Permissions"]
    )
    fun onVerifyPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "**Verification**",
            "Interact with the button below to verify yourself!",
            "",
            "**INFO:**",
            " - Not receiving a message? Make sure you have private messages enabled!",
            " - Have any other questions? Create a ticket in <#942211006199906354>!"
        ).apply {
            setImage("https://cdn.discordapp.com/attachments/918716751137603644/945082006600056932/unknown.png")
        }

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "verification",
                    "Verify yourself"
                )
            )
            .queue()
    }

    @Command("ticket-panel")
    @RolePermission(
        names = ["Permissions"]
    )
    fun onTicketPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "**Support**",
            "Interact with the button below to create a support ticket!",
            "",
            "**INFO:**",
            " - Please wait patiently as one of our staff members respond.",
            " - Please respond to all questions (staff members will not respond to the ticket unless they are complete)!"
        ).apply {
            setImage("https://cdn.discordapp.com/attachments/918716751137603644/945083169961558106/unknown.png")
        }

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "ticket-creation",
                    Emoji.fromUnicode("\uD83C\uDF9F")
                ).withLabel("Create a ticket")
            )
            .queue()
    }
}
