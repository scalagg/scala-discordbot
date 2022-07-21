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
    val guild: Long = 988902759434686474L,
    val activities: List<ScalaDiscordActivity> = mutableListOf(
        ScalaDiscordActivity(Activity.ActivityType.PLAYING, "potpvp.com"),
        ScalaDiscordActivity(Activity.ActivityType.COMPETING, "a tournament"),
        ScalaDiscordActivity(Activity.ActivityType.WATCHING, "over the discord"),
    ),
    private val supportRoles: String = "Platform Admin,Head Admin,Admin,Senior Mod,Developer"
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
