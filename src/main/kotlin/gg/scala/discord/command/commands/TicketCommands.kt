package gg.scala.discord.command.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.build.Commands.user
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.jda.annotation.RolePermission
import revxrsal.commands.jda.core.BaseActorJDA


/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@RolePermission(
    names = ["Support Team"]
)
@Command("ticket")
object TicketCommands
{
    @Default
    fun onDefault(actor: BaseActorJDA)
    {
        actor.reply(
            """
                **Ticket Commands:**
                 `-ticket add`: Add a user to the current ticket.
            """.trimIndent()
        )
    }

    @Subcommand("add")
    @Description("Add a user to the current ticket.")
    fun onAdd(actor: BaseActorJDA, member: Member)
    {
        if (!actor.channel.name.startsWith("ticket"))
        {
            throw CommandErrorException("This is not a ticket channel.")
        }

        val channel = actor.guild
            .getTextChannelById(actor.channel.id)
            ?: throw CommandErrorException(
                "This is not a valid channel."
            )

        channel.createPermissionOverride(member)
            .setAllow(Permission.VIEW_CHANNEL)
            .setAllow(Permission.MESSAGE_SEND)
            .queue {
                actor.reply("Added user ${member.asMention} to this ticket!")
            }
    }
}
