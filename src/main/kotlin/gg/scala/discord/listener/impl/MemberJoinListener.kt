package gg.scala.discord.listener.impl

import gg.scala.discord.listener.ScalaDiscordListenerService
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
            .getTextChannelsByName("arrivals", true)
            .firstOrNull()

        val member = event.guild
            .getRolesByName("Default", true)
            .firstOrNull()

        if (member != null)
        {
            event.member.guild.addRoleToMember(event.member, member).queue()
        }

        ScalaDiscordListenerService.mappings[event.member.id] = event.member
//
//        channel?.sendMessage(
//            "Welcome to the **PotPvP Network** Discord Server, " + event.member.asMention + "!"
//        )?.queue()
    }
}
