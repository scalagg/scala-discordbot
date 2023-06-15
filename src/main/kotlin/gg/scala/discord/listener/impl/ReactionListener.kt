package gg.scala.discord.listener.impl

import gg.scala.discord.reaction.ReactionMappings
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ReactionListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.channel.name == "server-roles")
        {
            ReactionMappings.values()
                .forEach {
                    event.message
                        .addReaction(
                            Emoji.fromUnicode(it.emoji)
                        )
                        .queue()
                }
        }
    }

    override fun onMessageReactionRemove(
        event: MessageReactionRemoveEvent
    )
    {
        if (event.reaction.channel.name != "server-roles")
        {
            return
        }

        val mapping = ReactionMappings
            .EMOJIS[event.reaction.emoji.asUnicode().asReactionCode]
            ?: return

        val member = event.member
            ?: return

        val role = event.guild
            .getRoleById(mapping.roleId)
            ?: return

        event.guild
            .removeRoleFromMember(
                member, role
            )
            .queue()
    }

    override fun onMessageReactionAdd(
        event: MessageReactionAddEvent
    )
    {
        if (event.reaction.channel.name != "server-roles")
        {
            return
        }

        val mapping = ReactionMappings
            .EMOJIS[event.emoji.asReactionCode]
            ?: return

        val member = event.member
            ?: return

        val role = event.guild
            .getRoleById(mapping.roleId)
            ?: return

        event.guild
            .addRoleToMember(
                member, role
            )
            .queue()
    }
}
