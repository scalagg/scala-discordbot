package gg.scala.discord.command.commands

import gg.scala.discord.ScalaDiscordApp
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
    /*@Command("verification-panel")
    @RolePermission(
        names = ["Developer", "Owner", "Permissions"]
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
        names = ["Developer", "Owner", "Permissions"]
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
        names = ["Developer", "Owner", "Permissions"]
    )
    fun onSyncPanel(actor: BaseActorJDA)
    {
        val embed = embed(
            "\uD83D\uDD17 **Discord Link**",
            "Link your Minecraft and Discord account!",
            "",
            "**How do I link my account?**",
            " - Log-in to our Minecraft server using `${ScalaDiscordApp.settings.serverAddress}`.",
            " - Run the `/link` command, and note down the alphanumeric code.",
            " - Click the **Link your account** button below and paste your code in <@875396948432154704>'s direct messages.",
            "",
            "**Need help?**",
            "Create a ticket in <#1038867550706090136> if you face any issues or have any questions during the linking process!"
        )

        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "discord-syncing", "Link your account"
                )
            )
            .queue()
    }

    @Command("whitelist-token-panel", "Permissions")
    @RolePermission(
        names = ["Developer", "Owner"]
    )
    fun onClaimWhitelistToken(actor: BaseActorJDA)
    {
        val embed = embed(
            "\uD83D\uDD17 **Claim Whitelist Tokens**",
            "Claim your whitelist token for you, or another player!",
            "",
            "**How do I get whitelist tokens?**",
            "Boost this discord server! Every individual boost gives you 1 additional whitelist token.",
            "",
            "**Need help?**",
            "Create a ticket in <#1008098345568448529> if you face any issues or have any questions during the linking process!"
        )


        actor.channel.sendMessageEmbeds(embed.build())
            .setActionRow(
                Button.success(
                    "discord-syncing", "Link your account"
                )
            )
            .queue()
    }*/
}
