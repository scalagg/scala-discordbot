package gg.scala.discord.listener.impl

import gg.scala.discord.ScalaDiscordApp
import gg.scala.discord.reaction.ReactionMappings
import kong.unirest.Unirest
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 2/5/2022
 */
object ReactionListener : ListenerAdapter()
{
    override fun onMessageReceived(event: MessageReceivedEvent)
    {
        if (event.message.channel.name == "✨┃server-roles")
        {
            ReactionMappings.values()
                .forEach {
                    event.message
                        .addReaction(
                            Emoji.fromUnicode(it.emoji)
                        )
                        .queue()
                }
        }
    }

    override fun onMessageReactionRemove(
        event: MessageReactionRemoveEvent
    )
    {
        if (event.reaction.channel.name != "✨┃server-roles")
        {
            return
        }

        val mapping = ReactionMappings
            .EMOJIS[event.reaction.emoji.asUnicode().asReactionCode]
            ?: return

        val member = event.member
            ?: return

        val role = event.guild
            .getRoleById(mapping.roleId)
            ?: return

        event.guild
            .removeRoleFromMember(
                member, role
            )
            .queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent)
    {
        if (event.name == "claimrank")
        {
            event.deferReply(true).queue()

            val transactionId = event.getOption("transaction-id")!!.asString
            data class Package(val id: Int, val name: String)
            data class Response(
                val status: String,
                val packages: List<Package>,
                val error_code: String? = null
            )

            CompletableFuture
                .supplyAsync {
                    val thing = Unirest.get("https://plugin.tebex.io/payments/$transactionId")
                        .header("accept", "application/json")
                        .header("X-Tebex-Secret", ScalaDiscordApp.settings.tebexApiKey)
                        .asString()
                        .body

                    if (thing == "[]")
                    {
                        return@supplyAsync Response("", listOf())
                    }


                    Serializers
                        .gson
                        .fromJson(
                            thing,
                            Response::class.java
                        )
                }
                .thenAccept { response ->
                    if (response.error_code != null)
                    {
                        event.hook
                            .sendMessage(
                                "We seem to have an internal issue. Please try again later!"
                            )
                            .queue()
                        return@thenAccept
                    }

                    if (response.status != "Complete")
                    {
                        event.hook
                            .sendMessage(
                                "We have received your purchase, however, it is still being processed. Please try again later!"
                            )
                            .queue()
                        return@thenAccept
                    }

                    val pkg = response.packages.firstOrNull { pkg -> (pkg.name == "Prime" || pkg.name == "Mythic") }

                    if (pkg == null)
                    {
                        event.hook
                            .sendMessage(
                                "Sorry, but your purchase does not include a role in our Discord server."
                            )
                            .queue()
                        return@thenAccept
                    }
                    val role = event.guild!!.getRolesByName(pkg.name, true).first()

                    if (event.member!!.roles.contains(role))
                    {
                        event.hook
                            .sendMessage(
                                "You have already claimed your role!"
                            )
                            .queue()
                        return@thenAccept
                    }

                    event.hook
                        .sendMessage(
                            "**Your purchase contains a Discord role.** You will be receiving the `${
                                pkg.name
                            }` role in our Discord server shortly."
                        )
                        .queue()

                    event.guild!!
                        .addRoleToMember(
                            event.user,
                            event.guild!!.getRolesByName(pkg.name, true).first()
                        )
                        .queue()
                }
                .exceptionally {
                    event.hook
                        .sendMessage(
                            "Failed! ${it.message}"
                        )
                        .queue()
                    return@exceptionally null
                }
        }
    }

    override fun onMessageReactionAdd(
        event: MessageReactionAddEvent
    )
    {
        if (event.reaction.channel.name != "✨┃server-roles")
        {
            return
        }

        val mapping = ReactionMappings
            .EMOJIS[event.emoji.asReactionCode]
            ?: return

        val member = event.member
            ?: return

        val role = event.guild
            .getRoleById(mapping.roleId)
            ?: return

        event.guild
            .addRoleToMember(
                member, role
            )
            .queue()
    }
}
