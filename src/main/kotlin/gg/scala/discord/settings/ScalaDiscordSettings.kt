package gg.scala.discord.settings

import gg.scala.discord.activity.ScalaDiscordActivity
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
class ScalaDiscordSettings(
    val token: String = "",
    val guild: Long = 880621130233294919L,
    val activities: List<ScalaDiscordActivity> = mutableListOf(
        ScalaDiscordActivity(Activity.ActivityType.WATCHING, "over the discord")
    ),
    private val supportRoles: String = "Administrator"
)
{
    fun getSupportRoles(guild: Guild): List<Role>
    {
        return supportRoles.split(",")
            .mapNotNull {
                kotlin.runCatching {
                    guild.getRolesByName(it, true)[0]
                }.getOrNull()
            }
    }
}
