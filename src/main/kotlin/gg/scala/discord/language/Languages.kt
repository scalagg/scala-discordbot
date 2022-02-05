package gg.scala.discord.language

import gg.scala.discord.language.impl.English
import gg.scala.discord.language.impl.French
import gg.scala.discord.language.impl.Italian
import gg.scala.discord.language.impl.Spanish
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object Languages
{
    val languages = mutableListOf(
        Spanish, English, Italian, French
    )

    fun find(id: String): Language?
    {
        return languages.firstOrNull {
            it.getId().equals(id, true)
        }
    }
}
