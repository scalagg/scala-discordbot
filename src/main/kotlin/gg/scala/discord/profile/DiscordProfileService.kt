package gg.scala.discord.profile

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.controller.DataStoreObjectControllerCache

/**
 * @author GrowlyX
 * @since 8/21/2022
 */
@Service
object DiscordProfileService
{
    @Configure
    fun configure()
    {
        DataStoreObjectControllerCache.create<DiscordProfile>()
    }
}
