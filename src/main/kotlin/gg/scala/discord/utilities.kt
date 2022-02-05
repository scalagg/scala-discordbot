package gg.scala.discord

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
fun Guild.role(name: String): Role?
{
    return try
    {
        getRolesByName(name, false)[0]
    } catch (ignored: Exception)
    {
        null
    }
}
