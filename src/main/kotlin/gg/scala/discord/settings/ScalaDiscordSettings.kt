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
    val guild: Long = 927281666836283524L,
    val color: String = "#ffbc00",
    val activities: List<ScalaDiscordActivity> = mutableListOf(
        ScalaDiscordActivity(Activity.ActivityType.PLAYING, "na.verio.cc"),
        ScalaDiscordActivity(Activity.ActivityType.COMPETING, "a tournament"),
        ScalaDiscordActivity(Activity.ActivityType.WATCHING, "over the discord"),
    ),
    private val supportRoles: String = "Platform Admin,Head Admin,Admin,Senior Mod,Developer"
)
{
    fun getSupportRoles(guild: Guild): List<Role>
    {
        return supportRoles.split(",")
            .mapNotNull { guild.getRolesByName(it, true)[0] }
    }
}
