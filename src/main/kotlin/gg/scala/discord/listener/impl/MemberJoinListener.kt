package gg.scala.discord.listener.impl

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object MemberJoinListener : ListenerAdapter()
{
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent)
    {
        val channel = event.guild
            .getTextChannelsByName("arrivals", true)[0]!!

        event.guild
            .addRoleToMember(
                event.member,
                event.guild
                    .getRoleById(
                        "779419819056693249"
                    )!!
            )

        channel.sendMessage(
            "Welcome to the **Glade Network** Discord Server, " + event.member.asMention + "!"
        ).queue()
    }
}
