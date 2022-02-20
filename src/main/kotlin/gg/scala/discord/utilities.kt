package gg.scala.discord

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import java.awt.Color

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

fun embed(
    title: String,
    vararg description: String
): EmbedBuilder
{
    return EmbedBuilder().setTitle(title)
        .setDescription(description.joinToString(separator = "\n"))
        .setColor(Color.decode("#2f3136"))
}
