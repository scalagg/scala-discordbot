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
    val mongoURI: String = "mongodb://127.0.0.1:27017/admin",
    val mongoCollection: String = "Scala",
    val tebexApiKey: String = "",
    val redisHost: String = "127.0.0.1",
    val redisPort: Int = 6379,
    val redisPassword: String? = null,
    val serverName: String = "Minequest",
    val serverAddress: String = "minequest.gg",
    val guild: Long = 644729311282069524L,
    val sendLoginMessages: Boolean = true,
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
