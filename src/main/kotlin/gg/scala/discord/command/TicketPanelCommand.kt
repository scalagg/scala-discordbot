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
        names = ["SuperPerms"]
    )
    fun onVerifyPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "**Verification**",
            "Interact with the button below to verify yourself!",
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.secondary(
                    "verification",
                    "Verify yourself"
                )
            )
            .queue()
    }

    @Command("ticket-panel")
    @RolePermission(
        names = ["SuperPerms"]
    )
    fun onTicketPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "\uD83C\uDF9F **Support**",
            "Interact with the button below to create a new support ticket!",
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "ticket-creation",
                    Emoji.fromUnicode("\uD83C\uDF9F")
                ).withLabel("Create a Ticket")
            )
            .queue()
    }
}
