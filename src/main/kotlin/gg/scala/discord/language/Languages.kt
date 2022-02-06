package gg.scala.discord.language

import gg.scala.discord.language.impl.*
import gg.scala.flavor.service.Service

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
@Service
object Languages
{
    val languages = mutableListOf(
        Spanish, English, Italian, French, Dutch
    )

    fun find(id: String): Language?
    {
        return languages.firstOrNull {
            it.getId().equals(id, true)
        }
    }
}
