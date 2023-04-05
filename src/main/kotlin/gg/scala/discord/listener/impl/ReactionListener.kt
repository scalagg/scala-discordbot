package gg.scala.discord.listener.impl

import gg.scala.discord.reaction.ReactionMappings
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ReactionListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.channel.name == "roles")
        {
            ReactionMappings.values()
                .forEach {
                    event.message
                        .addReaction(it.emoji)
                        .queue()
                }
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent)
    {
        println(event.reactionEmote.emoji)
    }
}
