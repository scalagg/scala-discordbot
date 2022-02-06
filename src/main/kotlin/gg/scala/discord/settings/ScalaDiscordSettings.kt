package gg.scala.discord.settings

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
    private val supportRoles: String = "Platform Admin,Head Admin,Admin,Senior Mod,Developer"
)
{
    fun getSupportRoles(guild: Guild): List<Role>
    {
        return supportRoles.split(",")
            .mapNotNull { guild.getRolesByName(it, true)[0] }
    }
}
