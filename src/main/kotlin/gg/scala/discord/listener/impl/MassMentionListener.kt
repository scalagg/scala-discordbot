import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.time.Duration

/**
 * MassMentionListener prevents users from mass-mentioning others
 * and restricts them from pinging specific staff roles unless they have a staff role themselves.
 *
 * @author GrowlyX
 * @since 2/5/2022
 */
object MassMentionListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val userMentions = event.message.mentions.getMentions(Message.MentionType.USER)
        val roleMentions: List<Role> = event.message.mentions.roles

        if (userMentions.size >= 6) {
            event.message.delete().queue()
            event.guild.timeoutFor(event.member!!, Duration.ofDays(28)).queue()

            event.channel.sendMessage(
                "${event.member!!.asMention} has been muted for `pinging more than 6 users`."
            ).queue()
            return
        }

        val restrictedRoles = listOf("Practice Staff", "Practice Admin")
        val mentionedStaffRoles = roleMentions.any { it.name in restrictedRoles }
        val userRoles = event.member?.roles?.map { it.name } ?: emptyList()
        val isUserStaff = userRoles.any { it in restrictedRoles }

        if (mentionedStaffRoles && !isUserStaff) {
            event.message.delete().queue()
            event.channel.sendMessage(
                "${event.member!!.asMention}, please do not ping staff members."
            ).queue()
        }
    }
}