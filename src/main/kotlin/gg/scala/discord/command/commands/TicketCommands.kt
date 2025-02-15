package gg.scala.discord.command.commands

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.DefaultFor
import revxrsal.commands.annotation.Description
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.jda.annotation.RolePermission
import revxrsal.commands.jda.core.actor.BaseActorJDA

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Command("ticket")
object TicketCommands
{
    @DefaultFor("ticket")
    fun onDefault(actor: BaseActorJDA)
    {
        actor.reply(
            """
                **Ticket Commands:**
                 `-ticket add`: Add a user to the current ticket.
                 `-ticket remove`: Remove a user from the current ticket.
            """.trimIndent()
        )
    }

    @RolePermission(
        names = ["Practice Staff", "Practice Admin"]
    )
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

        channel.manager
            .putPermissionOverride(
                member,
                listOf(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND),
                listOf()
            )
            .queue {
                actor.reply("Added user ${member.asMention} to this ticket!")
            }
    }

    @RolePermission(
        names = ["Practice Staff", "Practice Admin"]
    )
    @Subcommand("remove")
    @Description("Remove a user from the current ticket.")
    fun onRemove(actor: BaseActorJDA, member: Member)
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

        channel.manager
            .removePermissionOverride(member)
            .queue {
                actor.reply("Removed user ${member.asMention} from this ticket.")
            }
    }
}
