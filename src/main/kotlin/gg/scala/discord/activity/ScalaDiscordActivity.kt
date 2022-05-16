package gg.scala.discord.activity

import net.dv8tion.jda.api.entities.Activity

/**
 * @author GrowlyX
 * @since 2/6/2022
 */
data class ScalaDiscordActivity(
    val type: Activity.ActivityType,
    val value: String,
    val url: String = ""
)
